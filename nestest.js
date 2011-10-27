/*
 * Runs the nestest.nes ROM and compares the output with a known good log.
 */

require("./ROM");
require("./Opcodes");
require("./Memory");
require("./6502");

ROM.loadFile("./nestest.nes");

CPU.init();
CPU.reset();

CPU.context.pc = 0xC000;

for(var _index = 0; _index < 2; _index++)
{
	CPU.step();
}

