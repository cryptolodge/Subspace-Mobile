/*
SubspaceMobile - A subspace/continuum client for mobile phones
Copyright (C) 2011 Kingsley Masters
Email: kshade2001 at users.sourceforge.net

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.


REVISIONS: 
 */

package com.subspace.random;

//CONVERTED FROM CATID's MERV BOT (www.mervbot.com)
public class SubspaceHeavyLCG {

    static final long KSGSCM = 0x834E0B5FL;
    static final long KSGSCD = 0x1F31DL;
    long seed;					// Seed

    public SubspaceHeavyLCG() // Default seed(0)
    {
        seed(0);
    }

    public SubspaceHeavyLCG(int newSeed) // Provide a seed
    {
        seed(newSeed);
    }


    //thanks to TWCore.org for this implementation
    //thank you :)
    public short getNextE() // Get the next number for encryption
    {
        // Original C++ implementation contributed by UDP
        long old_seed = seed;

        seed = ((old_seed * KSGSCM) >> 48) & 0xffffffffL;
        seed = ((seed + (seed >> 31)) & 0xffffffffL);
        seed = ((((old_seed % KSGSCD) * 16807) - (seed * 2836) + 123) & 0xffffffffL);
        if( seed > 0x7fffffffL ){
            seed = ((seed + 0x7fffffffL) & 0xffffffffL);
        }

        return (short) (seed);
    }

    public short getNextG() // Get the next number for green seeds
    {
        return -1;
    }

    public void seed(int newSeed) // Provide a seed
    {
        seed = ((long)newSeed) & 0x00000000FFFFFFFFL;
    }
}

