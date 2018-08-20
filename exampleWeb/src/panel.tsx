import * as React from 'react';
import {CodeCtrl} from './codeCtrl';
import {CodeState} from './codeState';

interface PanelProps {
  ctrl : Readonly<CodeCtrl>
}

export class CodvPanel extends React.Component<PanelProps, CodeState> {

  constructor(props) {
    super(props);
    this.state = props.ctrl.state;
  }

  public componentDidMount() {

  }

  login() {
    console.log('login');
    this.props.ctrl.login();
  }

  public render() {
    return (
      <div className="codv-panel">
        <button onClick={() => this.login()}>login</button>
        <button>compile</button>
        <button>reload</button>
        <p>session: {this.state.getSessionId()} </p>
      </div>

    );
  }

};

