import React, { Component } from 'react';
import {browserHistory} from  'react-router';

export default class Login extends Component {

    constructor(props){
        super(props);
        this.state = {
            msg: this.props.location.query.msg
        };
    }

    componentDidMount(){
        console.log("componentDidMount");
    }

    envia(event){
        console.log('envia', this.state);
        event.preventDefault();

        const url = 'http://localhost:8080/meuorcamento/api/usuario/login';
        const req = {
            method: 'PUT',
            body: JSON.stringify({ login: this.login.value, senha: this.senha.value }),
            headers: new Headers({ 'Content-type' : 'application/json' })
        }

        fetch(url, req)
            .then(res => {
                if(res.ok){
                    return res.text();
                }else{
                    throw new Error('não foi possível fazer o login');
                }
            }).then(jsonStringToken => {
                const token = JSON.parse(jsonStringToken).authtoken;
                localStorage.setItem('auth-token', token);
                browserHistory.push('/home');
            }).catch(error => {
                this.setState({
                    msg: error.message
                });
            });

    }

    render(){ 
        return (
            <div className="login-box">
                <h1 className="header-logo">Meu Orçamento</h1>
                <span className="negativo">{this.state.msg}</span>
                <form onSubmit={this.envia.bind(this)} > 
                    <input id="login" type="text" placeholder="login" ref={(input) => this.login = input} />
                    <input id="senha" type="password" placeholder="senha" ref={(input) => this.senha = input} />
                    <button type="submit" className="pure-button">login</button>
                </form>
            </div>
        );
  }   
}