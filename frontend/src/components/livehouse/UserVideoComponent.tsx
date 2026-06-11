import React from 'react';
import OpenViduVideoComponent from './OvVideo';
import { StreamManager } from 'openvidu-browser';
import styles from './UserVideo.module.css';

interface UserVideoComponentProps {
  streamManager: StreamManager;
}

const UserVideoComponent: React.FC<UserVideoComponentProps> = ({ streamManager }) => {
  const getNicknameTag = () => {
    // Gets the nickName of the user
    return JSON.parse(streamManager.stream.connection.data).clientData;
  };

  return (
    <div>
      {streamManager && (
        <div className={styles.streamcomponent}>
          <OpenViduVideoComponent streamManager={streamManager} />
          <div><p>{getNicknameTag()}</p></div>
        </div>
      )}
    </div>
  );
};

export default UserVideoComponent;