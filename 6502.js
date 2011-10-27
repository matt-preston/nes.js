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

function CPUContext()
{
}


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

CPUCore.prototype.execute = function(aNumberOfCycles)
{
	var _clocksRemain = aNumberOfCycles;

	// Set up local variables with register values
	var _pc = this.context.pc;
	var _sp = this.context.sp;
	var _a  = this.context.a;
	var _x  = this.context.x;
	var _y  = this.context.y;
	var _p  = this.context.p;

	// TODO

	while(_clocksRemain > 0)
	{
	    var _opcode = readMemory(_pc);

        // Work out how many clock cycles this instruction takes
        _clocksRemain = _clocksRemain - this.context.opcodes.cycleCount[_opcode];

	    switch(_opcode)
	    {
	        // TODO
	    }
	}

    // Update the context with the new register values
    this.context.pc = _pc;
    this.context.sp = _sp;
    this.context.a  = _a;
    this.context.x  = _x;
    this.context.y  = _y;
    this.context.p  = _p;

    return _clocksRemain;
};