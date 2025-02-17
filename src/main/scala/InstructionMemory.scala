package chiselv

import chisel3._
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import chisel3.util.log2Ceil

class InstructionMemPort(val bitWidth: Int, val sizeBytes: Long) extends Bundle {
  val readAddr = Input(UInt(log2Ceil(sizeBytes).W))
  val readData = Output(UInt(bitWidth.W))
}

class InstructionMemory(
  bitWidth:   Int = 32,
  sizeBytes:  Long = 1,
  memoryFile: String = "",
) extends Module {
  val words = sizeBytes / bitWidth
  val io = IO(new Bundle() {
    val memPort = new InstructionMemPort(bitWidth, sizeBytes)
  })

  // This is required to have readmem outside `ifndef SYNTHESIS` and be synthesized by FPGA tools
  annotate(new ChiselAnnotation { override def toFirrtl = firrtl.annotations.MemorySynthInit })

  val mem = Mem(words, UInt(bitWidth.W))
  // Divide memory address by 4 to get the word due to pc+4 addressing
  val readAddress = io.memPort.readAddr >> 2
  if (memoryFile.trim().nonEmpty) {
    loadMemoryFromFileInline(mem, memoryFile)
  }
  io.memPort.readData := mem.read(readAddress)
}
