'use client';

import { useState, useEffect, useRef } from 'react';
import { OpenVidu, Publisher, Session, StreamEvent, StreamManager, Device } from 'openvidu-browser';
import axios from 'axios';

const APPLICATION_SERVER_URL = process.env.APPLICATION_SERVER_URL || process.env.NEXT_PUBLIC_API_BASE_URL;

export interface OpenViduState {
  mySessionId: string;
  myUserName: string;
  session: Session | undefined;
  mainStreamManager: StreamManager | undefined;
  publisher: Publisher | undefined;
  subscribers: StreamManager[];
  currentVideoDevice?: Device;
}

export const useOpenVidu = () => {
  const [state, setState] = useState<OpenViduState>({
    mySessionId: 'SessionA',
    myUserName: 'Participant' + Math.floor(Math.random() * 100),
    session: undefined,
    mainStreamManager: undefined,
    publisher: undefined,
    subscribers: [],
  });

  const OV = useRef<OpenVidu | null>(null);

  useEffect(() => {
    window.addEventListener('beforeunload', onBeforeUnload);
    return () => {
      window.removeEventListener('beforeunload', onBeforeUnload);
      leaveSession();
    };
  }, []);

  const onBeforeUnload = () => {
    leaveSession();
  };

  const handleChangeSessionId = (e: React.ChangeEvent<HTMLInputElement>) => {
    setState(prevState => ({
      ...prevState,
      mySessionId: e.target.value,
    }));
  };

  const handleChangeUserName = (e: React.ChangeEvent<HTMLInputElement>) => {
    setState(prevState => ({
      ...prevState,
      myUserName: e.target.value,
    }));
  };

  const handleMainVideoStream = (stream: StreamManager) => {
    if (state.mainStreamManager !== stream) {
      setState(prevState => ({
        ...prevState,
        mainStreamManager: stream
      }));
    }
  };

  const deleteSubscriber = (streamManager: StreamManager) => {
    const subscribers = state.subscribers;
    const index = subscribers.indexOf(streamManager, 0);
    if (index > -1) {
      subscribers.splice(index, 1);
      setState(prevState => ({
        ...prevState,
        subscribers: [...subscribers],
      }));
    }
  };

  const joinSession = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    
    // --- 1) Get an OpenVidu object ---
    OV.current = new OpenVidu();

    // --- 2) Init a session ---
    const session = OV.current.initSession();

    // --- 3) Specify the actions when events take place in the session ---
    // On every new Stream received...
    session.on('streamCreated', (event: StreamEvent) => {
      // Subscribe to the Stream to receive it
      const subscriber = session.subscribe(event.stream, undefined);
      
      // Update the state with the new subscribers
      setState(prevState => ({
        ...prevState,
        subscribers: [...prevState.subscribers, subscriber],
      }));
    });

    // On every Stream destroyed...
    session.on('streamDestroyed', (event: StreamEvent) => {
      // Remove the stream from 'subscribers' array
      deleteSubscriber(event.stream.streamManager);
    });

    // On every asynchronous exception...
    session.on('exception', (exception) => {
      console.warn(exception);
    });

    // --- 4) Connect to the session with a valid user token ---
    try {
      // Get a token from the OpenVidu deployment
      const token = await getToken();
      
      // Connect to the session
      await session.connect(token, { clientData: state.myUserName });
      
      // --- 5) Get your own camera stream ---
      const publisher = await OV.current.initPublisherAsync(undefined, {
        audioSource: undefined, // The source of audio. If undefined default microphone
        videoSource: undefined, // The source of video. If undefined default webcam
        publishAudio: true,     // Whether you want to start publishing with your audio unmuted or not
        publishVideo: true,     // Whether you want to start publishing with your video enabled or not
        resolution: '640x480',  // The resolution of your video
        frameRate: 30,          // The frame rate of your video
        insertMode: 'APPEND',   // How the video is inserted in target element 'video-container'
        mirror: false,          // Whether to mirror your local video or not
      });

      // --- 6) Publish your stream ---
      session.publish(publisher);

      // Obtain the current video device in use
      const devices = await OV.current.getDevices();
      const videoDevices = devices.filter(device => device.kind === 'videoinput');
      const currentVideoDeviceId = publisher.stream.getMediaStream().getVideoTracks()[0].getSettings().deviceId;
      const currentVideoDevice = videoDevices.find(device => device.deviceId === currentVideoDeviceId);

      setState(prevState => ({
        ...prevState,
        currentVideoDevice: currentVideoDevice,
        mainStreamManager: publisher,
        publisher: publisher,
        session: session,
      }));
      
    } catch (error) {
      console.log('There was an error connecting to the session:', error);
    }
  };

  const leaveSession = () => {
    if (state.session) {
      state.session.disconnect();
    }

    // Reset all properties
    OV.current = null;
    setState({
      mySessionId: 'SessionA',
      myUserName: 'Participant' + Math.floor(Math.random() * 100),
      session: undefined,
      subscribers: [],
      mainStreamManager: undefined,
      publisher: undefined,
    });
  };

  const switchCamera = async () => {
    try {
      if (!OV.current || !state.session) return;
      
      const devices = await OV.current.getDevices();
      const videoDevices = devices.filter(device => device.kind === 'videoinput');

      if (videoDevices && videoDevices.length > 1 && state.currentVideoDevice) {
        const newVideoDevices = videoDevices.filter(device => device.deviceId !== state.currentVideoDevice?.deviceId);

        if (newVideoDevices.length > 0 && state.mainStreamManager) {
          // Creating a new publisher with specific videoSource
          const newPublisher = OV.current.initPublisher(undefined, {
            videoSource: newVideoDevices[0].deviceId,
            publishAudio: true,
            publishVideo: true,
            mirror: true
          });

          // Unpublish current publisher and publish the new one
          await state.session.unpublish(state.mainStreamManager as Publisher);
          await state.session.publish(newPublisher);
          
          setState(prevState => ({
            ...prevState,
            currentVideoDevice: newVideoDevices[0],
            mainStreamManager: newPublisher,
            publisher: newPublisher,
          }));
        }
      }
    } catch (e) {
      console.error(e);
    }
  };

  /**
   * --------------------------------------------
   * GETTING A TOKEN FROM YOUR APPLICATION SERVER
   * --------------------------------------------
   */
  const getToken = async () => {
    const sessionId = await createSession(state.mySessionId);
    return await createToken(sessionId);
  };

  const createSession = async (sessionId: string) => {
    const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions', { customSessionId: sessionId }, {
      headers: { 'Content-Type': 'application/json' },
    });
    return response.data; // The sessionId
  };

  const createToken = async (sessionId: string) => {
    const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions/' + sessionId + '/connections', {}, {
      headers: { 'Content-Type': 'application/json' },
    });
    return response.data; // The token
  };

  return {
    state,
    handleChangeSessionId,
    handleChangeUserName,
    handleMainVideoStream,
    joinSession,
    leaveSession,
    switchCamera
  };
};