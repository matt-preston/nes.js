
Memory =
{
	_ram: [],

	readByte: function(anAddress)
	{
		return this._ram[anAddress];
	},

	readWord: function(anAddress)
	{
	},

	writeByte: function(aByte, anAddress)
	{
		this._ram[anAddress] = aByte;
	},

	writeWord: function(aWord, anAddress)
	{
	}
};