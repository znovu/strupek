import * as React from 'react';
import * as ReactDOM from 'react-dom';
import '../external/sample.js';
//import '../../js/target/scala-2.12/foo-fastopt';

import { Codv } from './codv';

ReactDOM.render(
  <Codv/>,
  document.getElementById('codv')
);