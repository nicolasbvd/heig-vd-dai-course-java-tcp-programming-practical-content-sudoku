# Encodings

This file describes how the clients and the server communicate grids and moves.

## Grid

A grid is a matrix of int value where negative value represent errors, 0 represent an empty cell, and bewteen 1 to `grid_size`. The remaining are errors. We have a `max_grid_size = 16`.  

The first byte of the bitmap describe the `grid_size` of the nxn grid. The remaining bytes are 1 byte for 1 cell. One could compress the data more but for these sizes of sudoku, it is useless.

## Move
```
SELECT B3 6 
```
Is encoded as an 3-byte array : 1 2 6.