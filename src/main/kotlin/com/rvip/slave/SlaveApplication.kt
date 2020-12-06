package com.rvip.slave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SlaveApplication

fun main(args: Array<String>) {
    runApplication<SlaveApplication>(*args)
}
