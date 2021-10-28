package com.github.Exemplo.zlbovolini

import com.github.M_odel.Hello
import com.github.Exemplo.zlbovolini.hello.ErrorDetails
import com.github.Exemplo.zlbovolini.hello.HelloGrpcReply
import com.github.Exemplo.zlbovolini.hello.HelloGrpcRequest
import com.github.Exemplo.zlbovolini.hello.HelloGrpcServiceGrpc
import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status.INVALID_ARGUMENT
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class HelloGrpcEndpoint(val helloRepository: HelloRepository) : HelloGrpcServiceGrpc.HelloGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun send(request: HelloGrpcRequest?, responseObserver: StreamObserver<HelloGrpcReply>?) {

        logger.info("Recebido requisição: $request")

        val name = request?.name

        if (name == null || name.isBlank()) {
            val error = INVALID_ARGUMENT.withDescription("nome deve ser informado")
                .asRuntimeException();
            responseObserver?.onError(error)
            return
        }

        if (name.uppercase().equals("LUCAS")) {
            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED_VALUE)
                .setMessage("Usuario sem permissao para acessar este recurso")
                .addDetails(Any.pack(
                    ErrorDetails.newBuilder()
                    .setCode(401)
                    .setMessage("Token expirado")
                    .build()))
                .build()

            val error = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(error)
            return
        }

        val hello = helloRepository.save(Hello(request.name))

        val response = HelloGrpcReply.newBuilder()
            .setId(hello.id!!)
            .setMessage("${request.name} says: Hello world!")
            .build()

        logger.info("Enviando resposta: $response")

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}