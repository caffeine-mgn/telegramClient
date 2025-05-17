package pw.binom.telegram.utils

import pw.binom.io.AsyncInput
import pw.binom.io.Available
import pw.binom.io.ByteBuffer
import pw.binom.io.DataTransferSize
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
internal class AutoClosableAsyncInput(
    private val source: AsyncInput,
    private val onFinish: suspend () -> Unit,
) : AsyncInput {
    private val closed = AtomicBoolean(false)
    override val available: Available
        get() = source.available

    private suspend fun callIsNeed(result: DataTransferSize): DataTransferSize {
        if (result.isNotAvailable && closed.compareAndSet(false, true)) {
            onFinish()
        }
        return result
    }

    override suspend fun read(dest: ByteBuffer) =
        callIsNeed(source.read(dest))

    override suspend fun asyncClose() {
        if (closed.compareAndSet(false, true)) {
            onFinish()
        }
        source.asyncClose()
    }

    override suspend fun read(
        dest: ByteArray,
        offset: Int,
        length: Int,
    ) = callIsNeed(
        source.read(
            dest = dest,
            offset = offset,
            length = length,
        )
    )
}