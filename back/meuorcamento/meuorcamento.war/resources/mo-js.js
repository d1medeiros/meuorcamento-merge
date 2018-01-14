
function getContaNext(){
	if($('#inserir-lista').find('tr').length > 0){
		console.log($('#inserir-lista'))
	}else{
		$('#inserir-lista').empty()
		$.get( "http://localhost:8080/meuorcamento/api/conta/proximo", function( data ) {
			console.log(data);
			$(data).each(function(index){
				
				var d = this.dataPagamento
				$('#inserir-lista')
				.append('<tr>')
				.append('<th id="lista-inp-id" scope="row">' + this.id + '</th>')
				.append('<td id="lista-inp-nome">' + this.nome + '</td')
				.append('<td id="lista-inp-valor">' + this.valor + '</td')
				.append('<td id="lista-inp-dtapagamento">' + d.dayOfMonth + '/' + d.monthValue + '/' + d.year + '</td')
				.append('<td id="lista-inp-estado">' + getEstado(this.estado) + '</td')
				.append('</tr>')
				
			})
		});
	}
}

function getConta(){
	if($('#inserir-lista').find('tr').length > 0){
		console.log($('#inserir-lista'))
	}else{
		$.get( "http://localhost:8080/meuorcamento/api/conta/atual", function( data ) {
			console.log(data);
			$(data).each(function(index){
				var d = this.dataPagamento
				
				//append mes atual
				$('#tr-label-lista').html('<th id="mes_' + d.monthValue + '" colspan="5">' + d.month + '</th>')
				
				//append cada item de conteudo
				$('#inserir-lista')
				.append('<tr>')
				.append('<th id="lista-inp-id" scope="row">' + this.id + '</th>')
				.append('<td id="lista-inp-nome">' + this.nome + '</td')
				.append('<td id="lista-inp-valor">' + this.valor + '</td')
				.append('<td id="lista-inp-dtapagamento">' + d.dayOfMonth + '/' + d.monthValue + '/' + d.year + '</td')
				.append('<td id="lista-inp-estado">' + getEstado(this.estado) + '</td')
				.append('</tr>')
				
			})
		});
	}
}

function getEstado(estado){
	var temp = '';
	  if(estado){
		  temp = 'concluido'
	  }else{
		  temp = 'aguardando'
	  }
	  return temp
}