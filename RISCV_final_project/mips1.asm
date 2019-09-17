

##################################### CODE SEGMENT #####################################################
.text
.globl main # main program entry
main:

    la a0, main
    mul t1, sp, gp
    addi	sp, sp, -4  # store the old return address in the stack 
divisible:
    addi	sp, sp, -4
    sw		ra, 0(sp)  # store the old return address in the stack 
loop1:
    lb 		t2, 0(sp)   # $t2 = number[i] where i is the current offset
    rem 	t3, t2, s2 # $t3 = number[i](mod $s2)
    bnez 	t3, next     # if number[i] is not devisible by the divisor in $s2
    addi	sp, sp, -4

    sw		a1, 0(sp)   # push $a1
    jal 	base          # gets here only if the element is devisible by the divisor
    lw		a1, 0(sp)   # pop $a1
    addi	sp, sp, 4
next:      # get the next array's element(goes from the end to the beggining of the array)
    addi	s1, s1, -1  # $ s1 = number + i - 1
    blt		s1, t4, loop1 # if $s1 < number
base:
    beq		s5, s4, next # if the current procedure is sum
    lbu		a1, 0(s1)	 # else restore the number to be printed as unsigned    
whatbase:

