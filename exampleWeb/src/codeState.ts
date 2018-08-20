
export class CodeState {
 private sessionId:  string = null;





 setSession(ses : string)  : CodeState{
   this.sessionId = ses;
   return this;
 }

 getSessionId() {
   return this.sessionId;
 }


}