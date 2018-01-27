import { Component } from 'react';


export default class Verificar extends Component {

    verifica(){
        const url = `http://localhost:8080/meuorcamento/api/usuario/verificar?XTOKEN=${localStorage.getItem('auth-token')}`;

        fetch(url)
        .then(res => {
            if(res.ok){
                return res.text();
            }else{
                throw new Error('não foi possível Verificar o login');
            }
        })
        .then(jsonStringToken => {
            const token = JSON.parse(jsonStringToken).authtoken;
            console.log('Verificar', token);
            localStorage.setItem('auth-token', token);
        }).catch(error => {
            this.setState({
                msg: error.message
            });
        });
       
    }

    render(){
        return null;
    }
}

