'use client';

import React from 'react';
import { StreamManager } from 'openvidu-browser';
import UserVideoComponent from './UserVideoComponent';

interface VideoContainerProps {
  mainStreamManager?: StreamManager;
  publisher?: StreamManager;
  subscribers: StreamManager[];
  handleMainVideoStream: (stream: StreamManager) => void;
}

const VideoContainer: React.FC<VideoContainerProps> = ({
  mainStreamManager,
  publisher,
  subscribers,
  handleMainVideoStream,
}) => {
  return (
    <>
      {mainStreamManager && (
        <div id="main-video">
          <UserVideoComponent streamManager={mainStreamManager} />
        </div>
      )}
      <div id="video-container">
        {publisher && (
          <div className="stream-container col-md-6 col-xs-6" onClick={() => handleMainVideoStream(publisher)}>
            <UserVideoComponent streamManager={publisher} />
          </div>
        )}
        {subscribers.map((sub, i) => (
          <div key={i} className="stream-container col-md-6 col-xs-6" onClick={() => handleMainVideoStream(sub)}>
            <UserVideoComponent streamManager={sub} />
          </div>
        ))}
      </div>
    </>
  );
};

export default VideoContainer;