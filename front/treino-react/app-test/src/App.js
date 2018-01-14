import React, { Component } from 'react';
import './css/pure-min.css';
import './css/marketing.css';
import {Link} from 'react-router';

class App extends Component {
  render() {
    return (

        <div>
        
            <div className="header">
                <div className="home-menu pure-menu pure-menu-horizontal pure-menu-fixed">
                    <a className="pure-menu-heading" href="/">Home</a>

                    <ul className="pure-menu-list">
                        <li className="pure-menu-item"><Link to="/contas" className="pure-menu-link">contas</Link></li>
                    </ul>
                </div>
            </div>

            <br /> 

            <h2 className="content-head is-center">Meu Orçamento</h2>

            <div id="main">
                    {this.props.children}
            </div>

            <div className="footer l-box is-center">
                Made with love by dmedeiros Team.
            </div>

        </div>
  );
  }
}

export default App;
