require("./Opcodes");
require("./Memory");
require("./6502");

CPU.init();
CPU.reset();

Memory.writeUnsignedByte(0xD8, 0x0000);  // CLD
Memory.writeUnsignedByte(0x18, 0x0001);  // CLC
Memory.writeUnsignedByte(0xA9, 0x0002);  // LDA
Memory.writeUnsignedByte(0x00, 0x0003);  //     #$00
Memory.writeUnsignedByte(0xA2, 0x0004);  // LDX
Memory.writeUnsignedByte(0x00, 0x0005);  //     #$00
Memory.writeUnsignedByte(0xA0, 0x0006);  // LDY
Memory.writeUnsignedByte(0x00, 0x0007);  //     #$00
Memory.writeUnsignedByte(0xC8, 0x0008);  // INY
Memory.writeUnsignedByte(0xD0, 0x0009);  // BNE
Memory.writeUnsignedByte(0xFD, 0x000a);  //     $0008
Memory.writeUnsignedByte(0xE8, 0x000b);  // INX
Memory.writeUnsignedByte(0xD0, 0x000c);  // BNE
Memory.writeUnsignedByte(0xF8, 0x000d);  //     $0006
Memory.writeUnsignedByte(0x69, 0x000e);  // ADC
Memory.writeUnsignedByte(0x01, 0x000f);  //     #$01
Memory.writeUnsignedByte(0xD0, 0x0010);  // BNE
Memory.writeUnsignedByte(0xF2, 0x0011);  //     $0004
Memory.writeUnsignedByte(0x00, 0x0012);  // BRK
Memory.writeUnsignedByte(0xED, 0x0013);  //

for(var _index = 0; _index < 10; _index++)
{
	CPU.step();
}



