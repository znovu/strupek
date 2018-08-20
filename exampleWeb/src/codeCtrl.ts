import {CodeState} from './codeState';

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
      console.log('uuu');
      let newState = this.state.setSession("a!" + this.getState().getSessionId());
      this.setState(newState);
      this.update(this);
  }

  getState() : CodeState {
    return this.state;
  }

  setState(newState : CodeState) : CodeCtrl {
    this.state = newState;
    return this;
  }

}