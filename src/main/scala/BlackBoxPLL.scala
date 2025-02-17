package chiselv

import chisel3._
import chisel3.util._

import scala.io.Source

class PLL0(board: String) extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle() {
    val clki = Input(Clock())
    val clko = Output(Clock());
    val lock = Output(Clock())
  })

  val filename = "pll_" + board + ".v"
  val verilog  = Source.fromResource(filename).getLines().mkString("\n")
  setInline("PLL.v", verilog)
}
