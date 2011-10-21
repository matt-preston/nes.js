
require("./Opcodes");
require("./Memory");
require("./6502");

CPU.init();
CPU.reset();

Memory.writeByte(0xD8, 0x0000);  // CLD
Memory.writeByte(0x18, 0x0001);  // CLC
Memory.writeByte(0xA9, 0x0002);  // LDA
Memory.writeByte(0x00, 0x0003);  //     #$00
Memory.writeByte(0xA2, 0x0004);  // LDX
Memory.writeByte(0x00, 0x0005);  //     #$00
Memory.writeByte(0xA0, 0x0006);  // LDY
Memory.writeByte(0x00, 0x0007);  //     #$00
Memory.writeByte(0xC8, 0x0008);  // INY
Memory.writeByte(0xD0, 0x0009);  // BNE
Memory.writeByte(0xFD, 0x000a);  //     $0008
Memory.writeByte(0xE8, 0x000b);  // INX
Memory.writeByte(0xD0, 0x000c);  // BNE
Memory.writeByte(0xF8, 0x000d);  //     $0006
Memory.writeByte(0x69, 0x000e);  // ADC
Memory.writeByte(0x01, 0x000f);  //     #$01
Memory.writeByte(0xD0, 0x0010);  // BNE
Memory.writeByte(0xF2, 0x0011);  //     $0004
Memory.writeByte(0x00, 0x0012);  // BRK
Memory.writeByte(0xED, 0x0013);  //

for(var _index = 0; _index < 100; _index++)
{
	CPU.step();
}

console.log("y is set to: " + CPU.context.y);