package com.github.Exemplo.zlbovolini

import com.github.Exemplo.zlbovolini.hello.HelloGrpcRequest
import io.micronaut.runtime.Micronaut.*
import java.io.FileInputStream
import java.io.FileOutputStream

fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.github.zlbovolini")
		.start()
}

fun example() {
	val request = HelloGrpcRequest.newBuilder()
		.setName("Hello World!")
		.build()

	println(request)

	// escreve em arquivo no formato binário
	request.writeTo(FileOutputStream("hello.bin"))

	// le do arquivo em formato binário
	val requestFromFile = HelloGrpcRequest.newBuilder()
		.mergeFrom(FileInputStream("hello.bin"))

	println(requestFromFile)
}

