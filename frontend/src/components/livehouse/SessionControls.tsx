'use client';

import React from 'react';

interface SessionControlsProps {
  sessionId: string;
  leaveSession: () => void;
  switchCamera: () => Promise<void>;
}

const SessionControls: React.FC<SessionControlsProps> = ({
  sessionId,
  leaveSession,
  switchCamera,
}) => {
  return (
    <div id="session-header">
      <h1 id="session-title">{sessionId}</h1>
      <input
        className="btn btn-large btn-danger"
        type="button"
        id="buttonLeaveSession"
        onClick={leaveSession}
        value="Leave session"
      />
      <input
        className="btn btn-large btn-success"
        type="button"
        id="buttonSwitchCamera"
        onClick={switchCamera}
        value="Switch Camera"
      />
    </div>
  );
};

export default SessionControls;