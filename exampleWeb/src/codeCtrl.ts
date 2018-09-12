import {CodeState} from './codeState';
//import * as  StrupekApi  from  '../../js/target/scala-2.12/foo-fastopt';

interface StrupekSessionProvider {
  startSession() : void;
}

declare var StrupekSessionProvider: StrupekSessionProvider;

export class CodeCtrl {
  state : CodeState = new CodeState();
  me = "codeCtrl";
  private update: (st: CodeCtrl) => void;

  constructor (upd: (code: CodeCtrl) => void) {
    this.update = upd;
  }

  init() {

  }

  login() {
    StrupekSessionProvider.startSession();
      setTimeout( () => {
        let newState = this.state.setSession("a!" + this.getState().getSessionId());
        this.setState(newState);
        this.update(this);
      }, 2000);
  }

  getState() : CodeState {
    return this.state;
  }

  setState(newState : CodeState) : CodeCtrl {
    this.state = newState;
    return this;
  }

}