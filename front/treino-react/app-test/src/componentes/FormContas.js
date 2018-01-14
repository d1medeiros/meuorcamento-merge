import React,{Component} from 'react';
import $ from 'jquery';

export default class FormContas extends Component{

    constructor(){
        super();
        this.state = {
            nome:'', 
            valor:0, 
            dataPagamento:'', 
            estado: false, 
            repetir: false, 
            tipoConta: '',
            alterarTodos: false,
            formAcao: 'salva'
        };
        this.salva = this.salva.bind(this);
    }
 
    sucessoAjax(resp){
        console.log(resp);
        return{
            nome: resp.nome, 
            valor: resp.valor, 
            dataPagamento: resp.dataPagamento, 
            estado: resp.estado, 
            repetir: resp.repetir, 
            tipoConta: resp.tipoConta,
            formAcao: 'altera'
        }
    }

    
    componentDidMount(){  
        console.log("didMount ");
        console.log(this.props);
        if(this.props.acao){

            $.ajax({
                url:"http://localhost:8080/meuorcamento/api/conta/" + this.props.acao,
                dataType: 'json',
                success:function(resp){    
                    
                    this.setState(this.sucessoAjax(resp));
                    
                }.bind(this)
            });
        }
    }   

    salva(event){
        event.preventDefault();
        var a = '';
        if(this.props.acao){
            a = this.state.alterarTodos ? 'altera/todos' : 'altera';
        }else{
            a = 'salva';
        }
        const tUrl = 'http://localhost:8080/meuorcamento/api/conta/' + a;
        
        console.log(this.props.acao);
        console.log(tUrl);

        $.ajax({
            type:'post',
            url: tUrl,
            contentType:'application/json',
            dataType:'json',
            data: JSON.stringify({
                id: this.props.acao,
                nome: this.state.nome,
                valor: this.state.valor,
                dataPagamento: this.state.dataPagamento,
                estado: this.state.estado,
                repetir: this.state.repetir,
                tipoConta: this.state.tipoConta
            }),
            success: function(){
                var acaoAltera = this.state.formAcao;
                this.setState({nome:'', valor:0, dataPagamento:'', estado: false, repetir: false, tipoConta: '', alterarTodos: false});
                if(acaoAltera === 'altera'){
                    this.props.rota.push('/contas')         
                }
                console.log(this.state)
            }.bind(this),
            error: function(res, req){
                console.log(res.status)
            }
            
        });
        
    }
    
    salvaAlteracao(nomeInput,event){
        var campoSendoAlterado = {};
        var value; 
        if(event.target.type === 'checkbox'){
            value = event.target.checked 
        } else if(event.target.type === 'number'){
            value = Number(event.target.value);
        }else{
           value = event.target.value;
        }
            
        campoSendoAlterado[nomeInput] = value;   
        this.setState(campoSendoAlterado);  
    }

    render(){
        return(
            <div>
                 <form className="pure-form pure-form-stacked" onSubmit={this.salva}>
                    <fieldset>
                        
                        <label htmlFor="nome">Nome</label>
                        <input id="nome" type="text" placeholder="nome da conta" value={this.state.nome} onChange={this.salvaAlteracao.bind(this,'nome')}/>
        
                        <label htmlFor="valor">Valor</label>
                        <input id="valor" type="number" placeholder="0000" value={this.state.valor} onChange={this.salvaAlteracao.bind(this,'valor')} />
        
                        <label htmlFor="dataPagamento">Data Pagamento</label>
                        <input id="dataPagamento" type="date"  value={this.state.dataPagamento} onChange={this.salvaAlteracao.bind(this,'dataPagamento')}/>
        
                        <div className="pure-controls">
                            <select value={this.state.tipoConta} name="tipoConta" onChange={this.salvaAlteracao.bind(this,'tipoConta')} >
                            <option value="">Selecione</option>
                            {this.props.tipos}
                            </select>
                        </div>

                        <label htmlFor="estado">Status</label>
                        <input id="estado" type="checkbox" checked={this.state.estado} onChange={this.salvaAlteracao.bind(this,'estado')} />
        
                        <div className={this.state.formAcao}>
                            <label  htmlFor="repetir">Repetir</label>
                            <input id="repetir" type="checkbox" checked={this.state.repetir} onChange={this.salvaAlteracao.bind(this,'repetir')} />
                        </div>
        
                        <div className={this.state.formAcao} id="alteraShow">
                            <label  htmlFor="repetir">Deve alterar em todos?</label>
                            <input id="alterarTodos" type="checkbox" checked={this.state.alterarTodos} onChange={this.salvaAlteracao.bind(this,'alterarTodos')} />
                        </div>

        
                        <button type="submit" className="pure-button">Enviar</button>
                    </fieldset>
                </form>
            </div>
        );
    }

}
