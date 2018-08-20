import * as React from 'react';
import {CodeState} from './codeState';
import {CodvPanel} from './panel';
import {CodeCtrl} from './codeCtrl';

interface Props {

}

export class Codv extends React.Component<Props, CodeCtrl> {

  constructor(props) {
    super(props);
    this.state =new CodeCtrl((code :CodeCtrl) => this.update(code));
  }



  public componentDidMount() {

  }

  update(newState : CodeCtrl) {
    console.log('setting state:'+ this.state);

    this.setState(this.state);
  }

  public render() {
    return (
      <div className="codv">
        <textarea defaultValue="{'qqqa'}"></textarea>
        <CodvPanel ctrl ={this.state}/>
      </div>
    );
  }

}




