package meuorcamento;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Teste {

	public static void main(String[] args) {
		String[] split = "12-14-16".split("-");
//		int[] array = Stream.of(split).mapToInt(Integer::getInteger).toArray();
		
		Stream.of(split).collect(Collectors.joining( "-" ));
		
		
//		.collect(Collectors.joining( "-" ));
		
//		.forEach(System.out::println);
		System.out.println(split.toString());

	}

}
