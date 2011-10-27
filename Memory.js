
Memory =
{
	_lowMem: [],
	_prom: [],

    readSignedByte: function(anAddress)
	{
		var _byte = this.readUnsignedByte(anAddress);

		if(_byte < 0x80)
        {
            return _byte;
        }
        else
        {
            return _byte - 256;
        }
	},

	readUnsignedByte: function(anAddress)
	{
		// Mask to 16 bit
		var _address = anAddress & 0xFFFF;

        if(_address < 0x2000)
        {
            // Low memory 2KB (mirrored 3 times)
            return this._lowMem[_address & 0x7FF];
        }
        else if (_address > 0x4017)
        {
            // Program ROM
            return this._prom[_address];
        }

        console.log("Don't know how to read from memory address [" + _address + "]");

        return 0;  // TODO
	},

	writeUnsignedByte: function(aByte, anAddress)
	{
		if (anAddress < 0x2000)
		{
            // Low memory 2KB (mirrored 3 times)
            this._lowMem[anAddress & 0x7FF] = aByte;

        }
        else if (anAddress > 0x4017)
        {
            // Program ROM
            this._prom[anAddress] = aByte;
        }
        else
        {
            console.log("Don't know how to write to memory at address [" + anAddress + "]");
        }
	}
};