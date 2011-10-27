var fs = require ('fs');

ROM =
{
	loadFile: function(aFilename)
	{
        var _buffer = fs.readFileSync(aFilename);

        // Read the header
        var _header = [];
        for(var _index = 0; _index < 16; _index++)
        {
            _header[_index] = _buffer[_index];
        }

        if(!((_header[0] === 0x4E) &&  // N
             (_header[1] === 0x45) &&  // E
             (_header[2] === 0x53) &&  // S
             (_header[3] === 0x1A)))   // <CR>
        {
            // Invalid ROM header.
            console.log("Invalid ROM header");
        }

        var _romCount   = _header[4];
        var _vromCount  = _header[5] * 2;
        var _mirroring  = ((_header[6] & 1) !== 0 ? 1 : 0);
        var _batteryRam = (_header[6] & 2) !== 0;
        var _trainer    = (_header[6] & 4) !== 0;
        var _fourScreen = (_header[6] & 8) !== 0;
        var _mapperType = (_header[6] >> 4) | (_header[7] & 0xF0);

        // Load PRG-ROM banks:
        var _offset = 16;
        var _bufferLength = _buffer.length;

        var _prom = [];
        for(var _romIndex = 0; _romIndex < _romCount; _romIndex++)
        {
            _prom[_romIndex] = []; // max size 16384
        }

        for (var _i = 0; _i < _romCount; _i++)
        {
            for (var _j = 0; _j < 16384; _j++)
            {
                if (_offset + _j >= _bufferLength)
                {
                    break;
                }

                _prom[_i][_j] = _buffer[_offset + _j];
            }
            _offset += 16384;
        }

        // Load prom into memory - the default mapper uses only a single bank, so
        // load it into both locations
        this.loadRomBank(_prom[0], 0x8000);
        this.loadRomBank(_prom[0], 0xC000);
	},

	loadRomBank: function(theBank, theAddress)
	{
        Utils.arraycopy(theBank, 0, Memory._prom, theAddress, 16384);
    }
};

Utils =
{
	arraycopy: function(theSource, theSourcePosition, theDestination, theDestinationPosition, theLength)
	{
		for(var _index = 0; _index < theLength; _index++)
		{
			theDestination[theDestinationPosition + _index] = theSource[theSourcePosition + _index];
		}
	}
};