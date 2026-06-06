package com.common.core.redis;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.batch.BatchExecutor;
import io.lettuce.core.dynamic.batch.BatchSize;
import io.lettuce.core.dynamic.batch.CommandBatching;

import java.util.List;
import java.util.Map;

/**
 * redis命令操作
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
@BatchSize(100)
public interface RedisBatchOperate extends Commands, BatchExecutor {

    RedisFuture<List<byte[]>> dump(byte[] key, CommandBatching batching);
    RedisFuture<Map<byte[], byte[]>> hgetall(byte[] key, CommandBatching batching);
    RedisFuture<List<Object>> hmget(byte[] key, List<Object> fields, CommandBatching commandBatching);
}
