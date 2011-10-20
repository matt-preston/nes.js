/*
 * MOS Technology 6502 core
 * Custom sound hardware and a restricted DMA controller on-die
 * Runs at 1.79 MHz
 * 8 bit, little-endian
 *
 * Registers a: accumulator
 *           x, y: index registers
 *           p: processor status
 */

CPU =
{
	context: undefined,

	init: function()
	{
		this.context = {};
	},

	reset: function()
	{
		this.context.a = 0;
        this.context.x = 0;
        this.context.y = 0;

        // flags
        this.context.carry     = false;
        this.context.zero      = false;
        this.context.irDisable = false;
        this.context.decimal   = false;
        this.context.brk       = false;
        this.context.overflow  = false;
        this.context.nagative  = false;

        // The processor status, based on the flags
        this.context.p = 0;

        this.context.sp = 0;
        this.context.pc = 0;
	},

    step: function()
    {
        this.execute(1);
    },

    execute: function(aNumberOfCycles)
    {
        var _clocksRemain = aNumberOfCycles;

        while(_clocksRemain > 0)
        {
            _clocksRemain--;

            console.log("PC now at: " + this.context.pc);

            var _opcode = Memory.readByte(this.context.pc);

            // Increment the program counter and mask
            this.context.pc = (this.context.pc + 1) & 0xFFFF;

            // Work out how many clock cycles this instruction takes
            //_clocksRemain = _clocksRemain - Opcodes.cycleCount[_opcode];



	        switch(_opcode)
	        {
                case 0x69: this.ADC(); break;
                case 0x65: this.ADC_zero_page(); break;
                case 0x75: this.ADC_zero_page_X(); break;
                case 0x6D: this.ADC_absolute(); break;
                case 0x7D: this.ADC_absolute_X(); break;
                case 0x29: this.AND(); break;
                case 0x25: this.AND_zero_page(); break;
                case 0x35: this.AND_zero_page_X(); break;
                case 0x2D: this.AND_absolute(); break;
                case 0x3D: this.AND_absolute_X(); break;
                case 0x39: this.AND_absolute_Y(); break;
                case 0x21: this.AND_indirect_X(); break;
                case 0x31: this.AND_indirect_Y(); break;
                case 0x0A: this.ASL(); break;
                case 0x06: this.ASL_zero_page(); break;
                case 0x16: this.ASL_zero_page_X(); break;
                case 0x0E: this.ASL_absolute(); break;
                case 0x1E: this.ASL_absolute_X(); break;
                case 0x90: this.BCC(); break;
                case 0xB0: this.BCS(); break;
                case 0xF0: this.BEQ(); break;
                case 0x24: this.BIT_zero_page(); break;
                case 0x2C: this.BIT_absolute(); break;
                case 0x30: this.BMI(); break;
                case 0xD0: this.BNE(); break;
                case 0x10: this.BPL(); break;
                case 0x00: this.BRK(); break;
                case 0x50: this.BVC(); break;
                case 0x70: this.BVS(); break;
                case 0x18: this.CLC(); break;
                case 0xD8: this.CLD(); break;
                case 0x58: this.CLI(); break;
                case 0xB8: this.CLV(); break;
                case 0xC9: this.CMP(); break;
                case 0xC5: this.CMP_zero_page(); break;
                case 0xD5: this.CMP_zero_page_X(); break;
                case 0xCD: this.CMP_absolute(); break;
                case 0xDD: this.CMP_absolute_X(); break;
                case 0xD9: this.CMP_absolute_Y(); break;
                case 0xC1: this.CMP_indirect_X(); break;
                case 0xD1: this.CMP_indirect_Y(); break;
                case 0xE0: this.CPX(); break;
                case 0xE4: this.CPX_zero_page(); break;
                case 0xEC: this.CPX_absolute(); break;
                case 0xC0: this.CPY(); break;
                case 0xC4: this.CPY_zero_page(); break;
                case 0xCC: this.CPY_absolute(); break;
                case 0xC6: this.DEC_zero_page(); break;
                case 0xD6: this.DEC_zero_page_X(); break;
                case 0xCE: this.DEC_absolute(); break;
                case 0xDE: this.DEC_absolute_X(); break;
                case 0xCA: this.DEX(); break;
                case 0x88: this.DEY(); break;
                case 0x49: this.EOR(); break;
                case 0x45: this.EOR_zero_page(); break;
                case 0x55: this.EOR_zero_page_X(); break;
                case 0x4D: this.EOR_absolute(); break;
                case 0x5D: this.EOR_absolute_X(); break;
                case 0x59: this.EOR_absolute_Y(); break;
                case 0x41: this.EOR_indirect_X(); break;
                case 0x51: this.EOR_indirect_Y(); break;
                case 0xE6: this.INC_zero_page(); break;
                case 0xF6: this.INC_zero_page_X(); break;
                case 0xEE: this.INC_absolute(); break;
                case 0xFE: this.INC_absolute_X(); break;
                case 0xE8: this.INX(); break;
                case 0xC8: this.INY(); break;
                case 0x4C: this.JMP(); break;
                case 0x6C: this.JMP_absolute(); break;
                case 0x20: this.JSR(); break;
                case 0xA9: this.LDA(); break;
                case 0xA5: this.LDA_zero_page(); break;
                case 0xB5: this.LDA_zero_page_X(); break;
                case 0xAD: this.LDA_absolute(); break;
                case 0xBD: this.LDA_absolute_X(); break;
                case 0xB9: this.LDA_absolute_Y(); break;
                case 0xA1: this.LDA_indirect_X(); break;
                case 0xB1: this.LDA_indirect_Y(); break;
                case 0xA2: this.LDX(); break;
                case 0xA6: this.LDX_zero_page(); break;
                case 0xB6: this.LDX_zero_page_Y(); break;
                case 0xAE: this.LDX_absolute(); break;
                case 0xBE: this.LDX_absolute_Y(); break;
                case 0xA0: this.LDY(); break;
                case 0xA4: this.LDY_zero_page(); break;
                case 0xB4: this.LDY_zero_page_X(); break;
                case 0xAC: this.LDY_absolute(); break;
                case 0xBC: this.LDY_absolute_X(); break;
                case 0x4A: this.LSR(); break;
                case 0x46: this.LSR_zero_page(); break;
                case 0x56: this.LSR_zero_page_X(); break;
                case 0x4E: this.LSR_absolute(); break;
                case 0x5E: this.LSR_absolute_X(); break;
                case 0xEA: this.NOP(); break;
                case 0x09: this.ORA(); break;
                case 0x05: this.ORA_zero_page(); break;
                case 0x15: this.ORA_zero_page_X(); break;
                case 0x0D: this.ORA_absolute(); break;
                case 0x1D: this.ORA_absolute_X(); break;
                case 0x19: this.ORA_absolute_Y(); break;
                case 0x01: this.ORA_indirect_X(); break;
                case 0x11: this.ORA_indirect_Y(); break;
                case 0x48: this.PHA(); break;
                case 0x08: this.PHP(); break;
                case 0x68: this.PLA(); break;
                case 0x28: this.PLP(); break;
                case 0x2A: this.ROL(); break;
                case 0x26: this.ROL_zero_page(); break;
                case 0x36: this.ROL_zero_page_X(); break;
                case 0x2E: this.ROL_absolute(); break;
                case 0x3E: this.ROL_absolute_X(); break;
                case 0x6A: this.ROR(); break;
                case 0x66: this.ROR_zero_page(); break;
                case 0x76: this.ROR_zero_page_X(); break;
                case 0x6E: this.ROR_absolute(); break;
                case 0x7E: this.ROR_absolute_X(); break;
                case 0x40: this.RTI(); break;
                case 0x60: this.RTS(); break;
                case 0xE9: this.SBC(); break;
                case 0xE5: this.SBC_zero_page(); break;
                case 0xF5: this.SBC_zero_page_X(); break;
                case 0xED: this.SBC_absolute(); break;
                case 0xFD: this.SBC_absolute_X(); break;
                case 0xF9: this.SBC_absolute_Y(); break;
                case 0xE1: this.SBC_indirect_X(); break;
                case 0xF1: this.SBC_indirect_Y(); break;
                case 0x38: this.SEC(); break;
                case 0xF8: this.SED(); break;
                case 0x78: this.SEI(); break;
                case 0x85: this.STA_zero_page(); break;
                case 0x95: this.STA_zero_page_X(); break;
                case 0x8D: this.STA_absolute(); break;
                case 0x9D: this.STA_absolute_X(); break;
                case 0x99: this.STA_absolute_Y(); break;
                case 0x81: this.STA_indirect_X(); break;
                case 0x91: this.STA_indirect_Y(); break;
                case 0x86: this.STX_zero_page(); break;
                case 0x96: this.STX_zero_page_Y(); break;
                case 0x8E: this.STX_absolute(); break;
                case 0x84: this.STY_zero_page(); break;
                case 0x94: this.STY_zero_page_X(); break;
                case 0x8C: this.STY_absolute(); break;
                case 0xAA: this.TAX(); break;
                case 0xA8: this.TAY(); break;
                case 0xBA: this.TSX(); break;
                case 0x8A: this.TXA(); break;
                case 0x9A: this.TXS(); break;
                case 0x98: this.TYA(); break;

                default: console.log("******* Unhandled opcode [" + _opcode + "]"); break;
            }
        }

        return _clocksRemain;
	},

    ADC: function()
    {
        console.log('opcode not implemented [ADC]');
    },

    ADC_zero_page: function()
    {
        console.log('opcode not implemented [ADC_zero_page]');
    },

    ADC_zero_page_X: function()
    {
        console.log('opcode not implemented [ADC_zero_page_X]');
    },

    ADC_absolute: function()
    {
        console.log('opcode not implemented [ADC_absolute]');
    },

    ADC_absolute_X: function()
    {
        console.log('opcode not implemented [ADC_absolute_X]');
    },

    AND: function()
    {
        console.log('opcode not implemented [AND]');
    },

    AND_zero_page: function()
    {
        console.log('opcode not implemented [AND_zero_page]');
    },

    AND_zero_page_X: function()
    {
        console.log('opcode not implemented [AND_zero_page_X]');
    },

    AND_absolute: function()
    {
        console.log('opcode not implemented [AND_absolute]');
    },

    AND_absolute_X: function()
    {
        console.log('opcode not implemented [AND_absolute_X]');
    },

    AND_absolute_Y: function()
    {
        console.log('opcode not implemented [AND_absolute_Y]');
    },

    AND_indirect_X: function()
    {
        console.log('opcode not implemented [AND_indirect_X]');
    },

    AND_indirect_Y: function()
    {
        console.log('opcode not implemented [AND_indirect_Y]');
    },

    ASL: function()
    {
        console.log('opcode not implemented [ASL]');
    },

    ASL_zero_page: function()
    {
        console.log('opcode not implemented [ASL_zero_page]');
    },

    ASL_zero_page_X: function()
    {
        console.log('opcode not implemented [ASL_zero_page_X]');
    },

    ASL_absolute: function()
    {
        console.log('opcode not implemented [ASL_absolute]');
    },

    ASL_absolute_X: function()
    {
        console.log('opcode not implemented [ASL_absolute_X]');
    },

    BCC: function()
    {
        console.log('opcode not implemented [BCC]');
    },

    BCS: function()
    {
        console.log('opcode not implemented [BCS]');
    },

    BEQ: function()
    {
        console.log('opcode not implemented [BEQ]');
    },

    BIT_zero_page: function()
    {
        console.log('opcode not implemented [BIT_zero_page]');
    },

    BIT_absolute: function()
    {
        console.log('opcode not implemented [BIT_absolute]');
    },

    BMI: function()
    {
        console.log('opcode not implemented [BMI]');
    },

    BNE: function()
    {
        console.log('opcode not implemented [BNE]');
    },

    BPL: function()
    {
        console.log('opcode not implemented [BPL]');
    },

    BRK: function()
    {
        console.log('opcode not implemented [BRK]');
    },

    BVC: function()
    {
        console.log('opcode not implemented [BVC]');
    },

    BVS: function()
    {
        console.log('opcode not implemented [BVS]');
    },

    CLC: function()
    {
        // Clear the carry flag
        this.context.carry = false;
    },

    CLD: function()
    {
        // Clear decimal mode.
        this.context.decimal = false;
    },

    CLI: function()
    {
        console.log('opcode not implemented [CLI]');
    },

    CLV: function()
    {
        console.log('opcode not implemented [CLV]');
    },

    CMP: function()
    {
        console.log('opcode not implemented [CMP]');
    },

    CMP_zero_page: function()
    {
        console.log('opcode not implemented [CMP_zero_page]');
    },

    CMP_zero_page_X: function()
    {
        console.log('opcode not implemented [CMP_zero_page_X]');
    },

    CMP_absolute: function()
    {
        console.log('opcode not implemented [CMP_absolute]');
    },

    CMP_absolute_X: function()
    {
        console.log('opcode not implemented [CMP_absolute_X]');
    },

    CMP_absolute_Y: function()
    {
        console.log('opcode not implemented [CMP_absolute_Y]');
    },

    CMP_indirect_X: function()
    {
        console.log('opcode not implemented [CMP_indirect_X]');
    },

    CMP_indirect_Y: function()
    {
        console.log('opcode not implemented [CMP_indirect_Y]');
    },

    CPX: function()
    {
        console.log('opcode not implemented [CPX]');
    },

    CPX_zero_page: function()
    {
        console.log('opcode not implemented [CPX_zero_page]');
    },

    CPX_absolute: function()
    {
        console.log('opcode not implemented [CPX_absolute]');
    },

    CPY: function()
    {
        console.log('opcode not implemented [CPY]');
    },

    CPY_zero_page: function()
    {
        console.log('opcode not implemented [CPY_zero_page]');
    },

    CPY_absolute: function()
    {
        console.log('opcode not implemented [CPY_absolute]');
    },

    DEC_zero_page: function()
    {
        console.log('opcode not implemented [DEC_zero_page]');
    },

    DEC_zero_page_X: function()
    {
        console.log('opcode not implemented [DEC_zero_page_X]');
    },

    DEC_absolute: function()
    {
        console.log('opcode not implemented [DEC_absolute]');
    },

    DEC_absolute_X: function()
    {
        console.log('opcode not implemented [DEC_absolute_X]');
    },

    DEX: function()
    {
        console.log('opcode not implemented [DEX]');
    },

    DEY: function()
    {
        console.log('opcode not implemented [DEY]');
    },

    EOR: function()
    {
        console.log('opcode not implemented [EOR]');
    },

    EOR_zero_page: function()
    {
        console.log('opcode not implemented [EOR_zero_page]');
    },

    EOR_zero_page_X: function()
    {
        console.log('opcode not implemented [EOR_zero_page_X]');
    },

    EOR_absolute: function()
    {
        console.log('opcode not implemented [EOR_absolute]');
    },

    EOR_absolute_X: function()
    {
        console.log('opcode not implemented [EOR_absolute_X]');
    },

    EOR_absolute_Y: function()
    {
        console.log('opcode not implemented [EOR_absolute_Y]');
    },

    EOR_indirect_X: function()
    {
        console.log('opcode not implemented [EOR_indirect_X]');
    },

    EOR_indirect_Y: function()
    {
        console.log('opcode not implemented [EOR_indirect_Y]');
    },

    INC_zero_page: function()
    {
        console.log('opcode not implemented [INC_zero_page]');
    },

    INC_zero_page_X: function()
    {
        console.log('opcode not implemented [INC_zero_page_X]');
    },

    INC_absolute: function()
    {
        console.log('opcode not implemented [INC_absolute]');
    },

    INC_absolute_X: function()
    {
        console.log('opcode not implemented [INC_absolute_X]');
    },

    INX: function()
    {
        console.log('opcode not implemented [INX]');
    },

    INY: function()
    {
        console.log('opcode not implemented [INY]');
    },

    JMP: function()
    {
        console.log('opcode not implemented [JMP]');
    },

    JMP_absolute: function()
    {
        console.log('opcode not implemented [JMP_absolute]');
    },

    JSR: function()
    {
        console.log('opcode not implemented [JSR]');
    },

    LDA: function()
    {
        console.log('opcode not implemented [LDA]');
    },

    LDA_zero_page: function()
    {
        console.log('opcode not implemented [LDA_zero_page]');
    },

    LDA_zero_page_X: function()
    {
        console.log('opcode not implemented [LDA_zero_page_X]');
    },

    LDA_absolute: function()
    {
        console.log('opcode not implemented [LDA_absolute]');
    },

    LDA_absolute_X: function()
    {
        console.log('opcode not implemented [LDA_absolute_X]');
    },

    LDA_absolute_Y: function()
    {
        console.log('opcode not implemented [LDA_absolute_Y]');
    },

    LDA_indirect_X: function()
    {
        console.log('opcode not implemented [LDA_indirect_X]');
    },

    LDA_indirect_Y: function()
    {
        console.log('opcode not implemented [LDA_indirect_Y]');
    },

    LDX: function()
    {
        console.log('opcode not implemented [LDX]');
    },

    LDX_zero_page: function()
    {
        console.log('opcode not implemented [LDX_zero_page]');
    },

    LDX_zero_page_Y: function()
    {
        console.log('opcode not implemented [LDX_zero_page_Y]');
    },

    LDX_absolute: function()
    {
        console.log('opcode not implemented [LDX_absolute]');
    },

    LDX_absolute_Y: function()
    {
        console.log('opcode not implemented [LDX_absolute_Y]');
    },

    LDY: function()
    {
        console.log('opcode not implemented [LDY]');
    },

    LDY_zero_page: function()
    {
        console.log('opcode not implemented [LDY_zero_page]');
    },

    LDY_zero_page_X: function()
    {
        console.log('opcode not implemented [LDY_zero_page_X]');
    },

    LDY_absolute: function()
    {
        console.log('opcode not implemented [LDY_absolute]');
    },

    LDY_absolute_X: function()
    {
        console.log('opcode not implemented [LDY_absolute_X]');
    },

    LSR: function()
    {
        console.log('opcode not implemented [LSR]');
    },

    LSR_zero_page: function()
    {
        console.log('opcode not implemented [LSR_zero_page]');
    },

    LSR_zero_page_X: function()
    {
        console.log('opcode not implemented [LSR_zero_page_X]');
    },

    LSR_absolute: function()
    {
        console.log('opcode not implemented [LSR_absolute]');
    },

    LSR_absolute_X: function()
    {
        console.log('opcode not implemented [LSR_absolute_X]');
    },

    NOP: function()
    {
        console.log('opcode not implemented [NOP]');
    },

    ORA: function()
    {
        console.log('opcode not implemented [ORA]');
    },

    ORA_zero_page: function()
    {
        console.log('opcode not implemented [ORA_zero_page]');
    },

    ORA_zero_page_X: function()
    {
        console.log('opcode not implemented [ORA_zero_page_X]');
    },

    ORA_absolute: function()
    {
        console.log('opcode not implemented [ORA_absolute]');
    },

    ORA_absolute_X: function()
    {
        console.log('opcode not implemented [ORA_absolute_X]');
    },

    ORA_absolute_Y: function()
    {
        console.log('opcode not implemented [ORA_absolute_Y]');
    },

    ORA_indirect_X: function()
    {
        console.log('opcode not implemented [ORA_indirect_X]');
    },

    ORA_indirect_Y: function()
    {
        console.log('opcode not implemented [ORA_indirect_Y]');
    },

    PHA: function()
    {
        console.log('opcode not implemented [PHA]');
    },

    PHP: function()
    {
        console.log('opcode not implemented [PHP]');
    },

    PLA: function()
    {
        console.log('opcode not implemented [PLA]');
    },

    PLP: function()
    {
        console.log('opcode not implemented [PLP]');
    },

    ROL: function()
    {
        console.log('opcode not implemented [ROL]');
    },

    ROL_zero_page: function()
    {
        console.log('opcode not implemented [ROL_zero_page]');
    },

    ROL_zero_page_X: function()
    {
        console.log('opcode not implemented [ROL_zero_page_X]');
    },

    ROL_absolute: function()
    {
        console.log('opcode not implemented [ROL_absolute]');
    },

    ROL_absolute_X: function()
    {
        console.log('opcode not implemented [ROL_absolute_X]');
    },

    ROR: function()
    {
        console.log('opcode not implemented [ROR]');
    },

    ROR_zero_page: function()
    {
        console.log('opcode not implemented [ROR_zero_page]');
    },

    ROR_zero_page_X: function()
    {
        console.log('opcode not implemented [ROR_zero_page_X]');
    },

    ROR_absolute: function()
    {
        console.log('opcode not implemented [ROR_absolute]');
    },

    ROR_absolute_X: function()
    {
        console.log('opcode not implemented [ROR_absolute_X]');
    },

    RTI: function()
    {
        console.log('opcode not implemented [RTI]');
    },

    RTS: function()
    {
        console.log('opcode not implemented [RTS]');
    },

    SBC: function()
    {
        console.log('opcode not implemented [SBC]');
    },

    SBC_zero_page: function()
    {
        console.log('opcode not implemented [SBC_zero_page]');
    },

    SBC_zero_page_X: function()
    {
        console.log('opcode not implemented [SBC_zero_page_X]');
    },

    SBC_absolute: function()
    {
        console.log('opcode not implemented [SBC_absolute]');
    },

    SBC_absolute_X: function()
    {
        console.log('opcode not implemented [SBC_absolute_X]');
    },

    SBC_absolute_Y: function()
    {
        console.log('opcode not implemented [SBC_absolute_Y]');
    },

    SBC_indirect_X: function()
    {
        console.log('opcode not implemented [SBC_indirect_X]');
    },

    SBC_indirect_Y: function()
    {
        console.log('opcode not implemented [SBC_indirect_Y]');
    },

    SEC: function()
    {
        console.log('opcode not implemented [SEC]');
    },

    SED: function()
    {
        console.log('opcode not implemented [SED]');
    },

    SEI: function()
    {
        console.log('opcode not implemented [SEI]');
    },

    STA_zero_page: function()
    {
        console.log('opcode not implemented [STA_zero_page]');
    },

    STA_zero_page_X: function()
    {
        console.log('opcode not implemented [STA_zero_page_X]');
    },

    STA_absolute: function()
    {
        console.log('opcode not implemented [STA_absolute]');
    },

    STA_absolute_X: function()
    {
        console.log('opcode not implemented [STA_absolute_X]');
    },

    STA_absolute_Y: function()
    {
        console.log('opcode not implemented [STA_absolute_Y]');
    },

    STA_indirect_X: function()
    {
        console.log('opcode not implemented [STA_indirect_X]');
    },

    STA_indirect_Y: function()
    {
        console.log('opcode not implemented [STA_indirect_Y]');
    },

    STX_zero_page: function()
    {
        console.log('opcode not implemented [STX_zero_page]');
    },

    STX_zero_page_Y: function()
    {
        console.log('opcode not implemented [STX_zero_page_Y]');
    },

    STX_absolute: function()
    {
        console.log('opcode not implemented [STX_absolute]');
    },

    STY_zero_page: function()
    {
        console.log('opcode not implemented [STY_zero_page]');
    },

    STY_zero_page_X: function()
    {
        console.log('opcode not implemented [STY_zero_page_X]');
    },

    STY_absolute: function()
    {
        console.log('opcode not implemented [STY_absolute]');
    },

    TAX: function()
    {
        console.log('opcode not implemented [TAX]');
    },

    TAY: function()
    {
        console.log('opcode not implemented [TAY]');
    },

    TSX: function()
    {
        console.log('opcode not implemented [TSX]');
    },

    TXA: function()
    {
        console.log('opcode not implemented [TXA]');
    },

    TXS: function()
    {
        console.log('opcode not implemented [TXS]');
    },

    TYA: function()
    {
        console.log('opcode not implemented [TYA]');
    }
};


/*
function CUPCore()
{
    this.context = undefined;
}

CPUCore.prototype.init = function()
{
	this.context.opcodes = initOpcodes();
};

CPUCore.prototype.reset = function()
{
    this.context.a = 0;
    this.context.x = 0;
    this.context.y = 0;
    this.context.p = 0;

    this.context.sp = 0;
    this.context.pc = 0;
};

CPUCore.prototype.setInterrupt = function()
{
};

CPUCore.prototype.step = function()
{
    this.execute(1);
};

CPUCore.prototype.execute = function(aNumberOfCycles)
{
	var _clocksRemain = aNumberOfCycles;

	while(_clocksRemain > 0)
	{
	    var _opcode = this.context.memory.readByte(this.context.pc);

        // Increment the program counter and mask
        this.context.pc = (this.context.pc + 1) & 0xFFFF;

        // Work out how many clock cycles this instruction takes
        //_clocksRemain = _clocksRemain - this.context.opcodes.cycleCount[_opcode];

	    switch(_opcode)
	    {

	    }
	}

    return _clocksRemain;
};
*/