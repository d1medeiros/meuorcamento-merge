import React from 'react';
import ReactDOM from 'react-dom';
import Cadastro from './Cadastro';
import Contas from './Contas';
import Home from './Home';
import App from './App';
import {Router,Route,browserHistory,IndexRoute} from 'react-router';
import './index.css';


ReactDOM.render(
  (<Router history={browserHistory}>
  	<Route path="/" component={App}>
  		<IndexRoute component={Home}/>
	  	<Route path="/cadastro" component={Cadastro}/>
	  	<Route path="/cadastro/:userId" component={Cadastro}/>
	  	<Route path="/contas" component={Contas}/>
  	</Route>
  </Router>),
  document.getElementById('root')
);