'use client';

import React from 'react';

interface JoinFormProps {
  mySessionId: string;
  myUserName: string;
  handleChangeSessionId: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleChangeUserName: (e: React.ChangeEvent<HTMLInputElement>) => void;
  joinSession: (e: React.FormEvent<HTMLFormElement>) => Promise<void>;
}

const JoinForm: React.FC<JoinFormProps> = ({
  mySessionId,
  myUserName,
  handleChangeSessionId,
  handleChangeUserName,
  joinSession,
}) => {
  return (
    <div id="join">
      <div id="img-div">
        <img src="/resources/images/openvidu_grey_bg_transp_cropped.png" alt="OpenVidu logo" />
      </div>
      <div id="join-dialog" className="jumbotron vertical-center">
        <h1> Join a video session </h1>
        <form className="form-group" onSubmit={joinSession}>
          <p>
            <label>Participant: </label>
            <input
              className="form-control"
              type="text"
              id="userName"
              value={myUserName}
              onChange={handleChangeUserName}
              required
            />
          </p>
          <p>
            <label> Session: </label>
            <input
              className="form-control"
              type="text"
              id="sessionId"
              value={mySessionId}
              onChange={handleChangeSessionId}
              required
            />
          </p>
          <p className="text-center">
            <input className="btn btn-lg btn-primary" name="commit" type="submit" value="JOIN" />
          </p>
        </form>
      </div>
    </div>
  );
};

export default JoinForm;