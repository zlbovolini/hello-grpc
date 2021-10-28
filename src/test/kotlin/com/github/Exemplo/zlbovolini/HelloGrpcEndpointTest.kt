package com.github.Exemplo.zlbovolini

import com.github.Exemplo.zlbovolini.hello.HelloGrpcRequest
import com.github.Exemplo.zlbovolini.hello.HelloGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false)
internal class HelloGrpcEndpointTest(
    val grpcClient: HelloGrpcServiceGrpc.HelloGrpcServiceBlockingStub,
    val helloRepository: HelloRepository
)
{
    @BeforeEach
    fun setUp() {
        helloRepository.deleteAll();
    }

    @Test
    fun `deve adicionar novo hello`() {

        val request = HelloGrpcRequest.newBuilder()
            .setName("Yuri Ponte")
            .build()
        val response = grpcClient.send(request)

        with(response) {
            assertNotNull(message)
            assertNotNull(id)
            assertTrue(helloRepository.existsById(id))
        }
    }

    @Test
    fun `nao deve adicionar hello para nome igual a Lucas ignorando case`() {

        val request = HelloGrpcRequest.newBuilder()
            .setName("Lucas")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            val response = grpcClient.send(request)
        }

        with(error) {
            assertEquals(Status.PERMISSION_DENIED.code, status.code)
            assertEquals("Usuario sem permissao para acessar este recurso", status.description)
        }
    }

    @Test
    fun `nao deve adicionar hello com dados invalidos`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.send(HelloGrpcRequest.newBuilder().build())
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("nome deve ser informado", status.description)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): HelloGrpcServiceGrpc.HelloGrpcServiceBlockingStub {
            return HelloGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}