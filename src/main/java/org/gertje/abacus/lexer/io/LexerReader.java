package org.gertje.abacus.lexer.io;

import java.io.IOException;
import java.io.Reader;

/**
 * LexerReader is een Reader met de volgende eigenschappen:
 *
 * - de reader kan bufferen
 * - de reader kan vooruit spieken
 * - de reader houdt regelnummers en kolomnummers bij
 * - de reader ondersteund markering
 * - de reader kan NIET een heel blok tekens ineens inlezen of tekens overslaan.
 */
public class LexerReader extends Reader {

	/**
	 * <pre>
	 * m = gemarkeerde index
	 * i = (lees) index
	 * l = aantal tekens in de buffer
	 * b = lengte van de buffer
	 * o = offset; het aantal tekens wat we minimaal verder willen lezen.
	 * 
	 * De buffer begint links, bij 0.
	 * 
	 * Buffer -->  #############################################################
	 *             ↑    ↑       ↑                      ↑                        ↑
	 *             0    m       i                      l                        b 
	 *                          |______________|
	 *                                 (A)
	 *                          |______________________________|
	 *                                        (B)
	 *                          |_______________________________________________________|
	 *                                                    (C)
	 * 
	 * We onderscheiden drie situaties:
	 * (A) i + o < l    In deze situatie kunnen we direct het teken teruggeven.
	 * (B) i + o < b    In deze situatie moeten we eerst een aantal tekens bijlezen; probeer tot het einde van de buffer
	 *                  te lezen.
	 * (C) i + o >= b   In deze situatie moeten we i proberen te verlagen. Als een markering actief is schuiven we de 
	 *                  data in de buffer m tekens naar links. (Verlaag dus m, i en l met m.) Anders schuiven we de data
	 *                  in de buffer i tekens naar links. (Verlaag dus i en l met i.) Wanneer daarna nog steeds geldt 
	 *                  dat i + o >= b maken we de buffer groter (met een veelvoud van blocksize).
	 * 
	 * In situatie (A) kan direct gelezen worden, voor situaties (B) en (C) roepen we eerst refreshBuffer() aan.
	 * </pre>
	 */

	/**
	 * De reader die we 'decoraten' met deze reader. (Zie het decorating pattern.)
	 */
    private Reader in;

    /**
     * De buffer waarin we de gegevens bufferen.
     */
    private char buffer[];
    
    /**
     * Het laatst gelezen teken.
     */
    private int previousChar;
    
    /**
     * Het nummer van de kolom van het huidige teken.
     */
    private int columnNumber;
    
    /**
     * Het nummer van de regel van het huidige teken.
     */
	private int lineNumber;
	
	/**
	 * De index van de buffer, wijst naar het volgende teken wat gelezen gaat worden.
	 */
	private int bufferIndex;
	
	/**
	 * Het aantal tekens in de buffer, geteld vanaf het begin van de buffer.
	 */
	private int numberOfCharactersInBuffer;

	/**
	 * De index van de markering.
	 */
	private int markedIndex;

	/**
	 * Het kolomnummer wat hoort bij de gemarkeerde index.
	 */
	private int markedColumnNumber;

	/**
	 * Het regelnummer wat hoort bij de gemarkeerde index.
	 */
	private int markedLineNumber;

	/**
	 * De standaard buffer grootte.
	 */
	private static int DEFAULT_BUFFER_SIZE = 2048;

	/**
	 * De blokgrootte, de buffer is altijd een veelvoud hiervan.
	 */
	private static int BLOCK_SIZE = 1024;

	/**
	 * Constructor.
	 * 
	 * @param in Reader die we decoraten.
	 * @param bufferSize De grootte van de buffer.
	 */
	public LexerReader(Reader in, int bufferSize) {
		super(in);
		if (bufferSize <= 0) {
			throw new IllegalArgumentException("bufferSize <= 0");
		}		
		this.in = in;
		buffer = new char[bufferSize];
		bufferIndex = numberOfCharactersInBuffer = 0;
		previousChar = -1;
		markedIndex = -1;
		lineNumber = 1;
		columnNumber = 1;
	}

	/**
	 * Constructor. Roept de andere constructor aan met de default buffer grootte.
	 * @param in
	 */
	public LexerReader(Reader in) {
		this(in, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Controleert of de input stream nog open is, wanneer dit niet het geval is gooit de methode een exceptie.
	 * @throws IOException
	 */
	private void checkInputStream() throws IOException {
		// Wanneer de input stream niet open is gooien we een exceptie.
		if (in == null) {
			throw new IOException("Closed input stream.");
		}
	}

	/**
	 * Sluit de reader en zorgt ervoor dat geheugen geruimd kan worden door de garbage collector.
	 * @throws IOException
	 */
	public void close() throws IOException {
		synchronized (lock) {
			// Wanneer de gewrapte reader al null is hoeven we verder niets te doen.
			if (in == null) {
				return;
			}
			// Sluit de gewrapte reader en maak variabalen null zodat het geheugen geruimd kan worden.
			in.close();
			in = null;
			buffer = null;
		}
	}

	/**
	 * Ververst de buffer. Dient alleen aangeroepen te worden wanneer de buffer leeg is.
	 * @return Het aantal bijgelezen tekens.
	 * @throws IOException Een exceptie wanneer de buffer vol zit.
	 */
	private int refreshBuffer(int offset) throws IOException {
		// Wanneer we in deze methode komen hebben we te maken met situatie (B) of (C).

		// Controleer of er genoeg ruimte vrij is na het laatste teken.
		if (bufferIndex + offset >= buffer.length) {
			// Er is NIET genoeg ruimte vrij na het laatste teken (we hebben te maken met situatie (C)).
			// Probeer ruimte te maken door de index te verlagen. Dit kunnen we alleen doen wanneer de index niet al 0 
			// is EN er geen markering is of de gemarkeerde index groter is dan 0. (Let op! We controleren we hier of 
			// markedIndex ONgelijk is aan 0, omdat -1 betekent dat er geen markering is.)
			if (bufferIndex != 0 && markedIndex != 0) {
				// Bepaal de plek vanaf waar we moeten schuiven, dit is de gemarkeerde index wanneer er een markering
				// actief is en anders is dit de index.
				int shiftFrom = markedIndex != -1 ? markedIndex : bufferIndex;
				// Schuif de gegevens in de array in de 0 richting.
				System.arraycopy(buffer, shiftFrom , buffer, 0, numberOfCharactersInBuffer - shiftFrom);
				// Update de markering wanneer deze actief is.
				if (markedIndex != -1) {
					markedIndex = 0;
				}
				// Update het aantal tekens in de buffer.
				numberOfCharactersInBuffer -= shiftFrom;
				// Update de index
				bufferIndex -= shiftFrom;
			}
		
			// Controleer opnieuw of we genoeg ruimte in de buffer hebben.
			if (bufferIndex + offset >= buffer.length) {
				// We hebben nog niet genoeg ruimte in de buffer. We moeten de buffer daarom vergroten.
				// Bepaal de nieuwe grootte van de buffer.
				int newBufferSize = bufferIndex + offset;
				// Wanneer newBufferSize niet een veelvoud van de blokgrootte is moeten we dit er van maken.
				if (newBufferSize % BLOCK_SIZE != 0) {
					newBufferSize = (newBufferSize / BLOCK_SIZE + 1) * BLOCK_SIZE;
				}
				char newBuffer[] = new char[newBufferSize];
				// Kopieer de gegevens van de oude buffer naar de nieuwe.
				System.arraycopy(buffer, 0 , newBuffer, 0, numberOfCharactersInBuffer);
				// Zet de verwijzing van de buffer.
				buffer = newBuffer;
			}
			
		}
		
		// Er is nu genoeg ruimte vrij na het laatste teken.
		// Bepaal de te lezen lengte.
		int readLength = buffer.length - numberOfCharactersInBuffer;
		int charactersRead = 0;

		// We lezen net zo lang tekens in totdat we tenminste offset tekens vooruit kunnen lezen.
		while (bufferIndex + offset >= numberOfCharactersInBuffer) {
			int n = in.read(buffer, numberOfCharactersInBuffer, readLength);
			if (n != -1) {
				numberOfCharactersInBuffer += n;
				readLength -= n;
				charactersRead += n;
			} else {
				// Wanneer we hier komen hebben we nog niet genoeg tekens ingelezen (anders waren we al uit de loop) en
				// is het einde van de in-stream bereikt. Dus we kunnen niet genoeg tekens inlezen, daarom moeten we -1
				// teruggeven.
				return n;
			}
		}

		// Geef het aantal gelezen tekens terug.
		return charactersRead;
	}

	@Override
	public int read() throws IOException {
		synchronized (lock) {
			checkInputStream();
			// Wanneer de index groter of gelijk is aan het aantal karakters in de buffer verversen we de buffer.
			if (bufferIndex >= numberOfCharactersInBuffer) {
				// Probeer de buffer te verversen, wanneer dit niet lukt geven we -1 terug.
				if (refreshBuffer(0) == -1) {
					return -1;
				}
			}
			
			int c = buffer[bufferIndex++];
			columnNumber++;

			// Wanneer het teken een \r is of een \n, die niet vooraf is gegaan door een \r, hogen we het regelnummer 
			// op.
			if (c == '\r' || (c == '\n' && previousChar != '\r')) {
				lineNumber++;
				columnNumber = 1;
			}

			// Verander het laatst gelezen karakter.
			previousChar = c;
			
			// Geef het karakter op de huidige index terug en hoog de index op.
			return c;
		}
	}

	/**
	 * Spiekt vooruit wat het volgende teken is.
	 * 
	 * @return Het volgende teken.
	 * @throws IOException
	 */
	public int peek() throws IOException {
		return peek(0);
	}
	
	/**
	 * Spiekt offset tekens vooruit.
	 * 
	 * @return Het teken offset tekens vooruit.
	 * @throws IOException
	 */
	public int peek(int offset) throws IOException {
		synchronized (lock) {
			checkInputStream();
			// Wanneer de index groter of gelijk is aan het aantal karakters in de buffer verversen we de buffer.
			if (bufferIndex + offset >= numberOfCharactersInBuffer) {
				// Probeer de buffer te verversen, wanneer dit niet lukt geven we -1 terug.
				if (refreshBuffer(offset) == -1) {
					return -1;
				}
			}

			// Geef het karakter op de huidige index terug en hoog de index NIET op.
			return buffer[bufferIndex + offset];
		}
	}
	
	@Override
    public boolean ready() throws IOException {
        synchronized (lock) {
            checkInputStream();
            return (bufferIndex < numberOfCharactersInBuffer) || in.ready();
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        synchronized (lock) {
            checkInputStream();
            markedIndex = bufferIndex;
            markedColumnNumber = columnNumber;
            markedLineNumber = lineNumber;
        }
    }

    @Override
    public void reset() throws IOException {
        synchronized (lock) {
            checkInputStream();
            if (markedIndex < 0) {
                throw new IOException("Stream not marked");
            }
            bufferIndex = markedIndex;
            markedIndex = -1;
            columnNumber = markedColumnNumber;
            lineNumber = markedLineNumber;
        }
    }

    /**
     * Leest een heel aantal tekens ineens in een array. DEZE METHODE IS NIET GEIMPLEMENTEERD.
     * @return Het aantal gelezen tekens.
     * @throws IOException
     */
	public int read(char cbuf[], int off, int len) throws IOException {
		throw new IOException("read() method is not supported in LexerReader.");
	}

    /**
     * Slaat een aantal tekens over. DEZE METHODE IS NIET GEIMPLEMENTEERD.
     * @return Het aantal overgeslagen tekens.
     * @throws IOException
     */	
	public long skip(long n) throws IOException {
		throw new IOException("skip() method is not supported in LexerReader.");
    }


	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
}