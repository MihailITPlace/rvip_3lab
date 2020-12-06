package com.rvip.slave.controllers

import com.rvip.slave.models.SortTask
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity



@RestController
class SumController {
    @PostMapping(value = ["/sum"])
    private fun sumNumbers(@RequestBody numbers: LongArray): Long {
        return numbers.sum()
    }

    @PostMapping(value = ["/sort"])
    private fun sortMatrix(@RequestBody task: SortTask): Array<LongArray>  {
        return runBlocking {
            val defs = mutableListOf<Deferred<LongArray>>()
            task.ports.forEachIndexed { i, p ->
                val d = async { getRowWithSum(p, task.matrix[i]) }
                defs.add(d)
            }

            defs.map { it.await() }.sortedBy { it[0] }.map { it.sliceArray(1 until it.size) }.toTypedArray()
        }
    }

    private fun getRowWithSum(port: Long, row: LongArray): LongArray {
        val res = RestTemplate().postForEntity<Long>("http://localhost:${port}/sum", row)
        if (res.body != null) {
            val ml = row.toMutableList()
            ml.add(0, res.body!!)
            return ml.toLongArray()
        }

        throw Exception("пустое тело ответа от слейва $port")
    }

}