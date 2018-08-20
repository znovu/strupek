
export class CodeState {
 private sessionId:  string = null;
  me = "codeState";




 setSession(ses : string)  : CodeState{
   this.sessionId = ses;
   return this;
 }

 getSessionId() {
   return this.sessionId;
 }


}