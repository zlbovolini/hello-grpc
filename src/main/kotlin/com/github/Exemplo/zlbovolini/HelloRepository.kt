package com.github.Exemplo.zlbovolini

import com.github.M_odel.Hello
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface HelloRepository: JpaRepository<Hello, Long> {
}