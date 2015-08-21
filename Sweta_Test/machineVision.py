from tkinter import Tk, Canvas, YES, BOTH, PhotoImage, NW
from time import clock
from random import choice
from math import sqrt, atan2, degrees
from types import *
root = Tk()
START = clock()
WIDTH = 1913
HEIGHT = 990
COLORFLAG = False
HIGH = 45
LOW = 10
NUMBER_OF_TIMES_TO_SMOOTH_IMAGE = 5

def main():
	file1 = open('2014June20PPM.ppm', 'r')
	stng = file1.readline()
	print(stng)
	nums = file1.read().split()
	print(nums[:10])
	file1.close()

	image = []
	for pos in range(0, len(nums), 3):
		RGB = (int(nums[pos+0]), int(nums[pos+1]), int(nums[pos+2]))
		image.append(int(0.2*RGB[0] + 0.7*RGB[1] + 0.1*RGB[2]))
	printElapsedTime('Gray numbers are now created.')

	#file1 = open('lenaGRAY.ppm', 'w')
	#for elt in image: 
		#file1.write(str(elt) + ' ')
	#printElapsedTime('saved file numbers')
	#file1.close()
	#displayImageInWindow(image) #gray scale image
	
	for r in range(NUMBER_OF_TIMES_TO_SMOOTH_IMAGE):
		image = gaussianBlur(image)
	printElapsedTime('applied gaussian blur')
	#displayImageInWindow(image) #blurred image
	
	image = sobelMask(image)
	printElapsedTime('applied sobel mask')

	image = cannyTransform(image)
	tmp = []
	for e in image:
		if e[4] == 1:
			tmp.append(0)
		else:
			tmp.append(255)
	image = normalize(tmp)
	# image = normalize([x[0] for x in image])
	printElapsedTime('canny transform')

#-- Display modified file as an image
	displayImageInWindow (image)
	#displayImageInWindow(x[0] for x in image)
	root.mainloop()
	
def gaussianBlur(image):
	image2 = [0] * WIDTH * HEIGHT
	fil = [1, 2, 1, 2, 4, 2, 1, 2, 1]
	pos = [-1-WIDTH, -WIDTH, 1-WIDTH, -1, 0, 1, -1+WIDTH, WIDTH, 1+WIDTH]
	for row in range(1, HEIGHT-1):
		for col in range(1, WIDTH-1):
			s = 0
			index = col+row*HEIGHT
			for x in range(len(fil)):
				s += fil[x]*image[index + pos[x]]
			image2[index] = round(s/16)
	return image2

def sobelMask(image):
	imagex = [0] * WIDTH * HEIGHT
	imagey = [0] * WIDTH * HEIGHT
	fily = [1, 2, 1, 0, 0, 0, -1, -2, -1]
	filx = [-1, 0, 1, -2, 0, 2, -1, 0, 1]
	pos = [-1-WIDTH, -WIDTH, 1-WIDTH, -1, 0, 1, -1+WIDTH, WIDTH, 1+WIDTH]
	for row in range(1, HEIGHT-1):
		for col in range(1, WIDTH-1):
			sx = sy = 0
			index = col+row*HEIGHT
			for x in range(len(pos)):
				sx += filx[x]*image[index + pos[x]]
				sy += fily[x]*image[index + pos[x]]
			imagex[index] = sx
			imagey[index] = sy
	image3 = [[sqrt(imagex[c]*imagex[c] + imagey[c]*imagey[c]), theta(imagex[c], imagey[c]), 0, 0, 0] for c in range(WIDTH*HEIGHT)]
	return image3

def theta(dx, dy):
	rawAngle = degrees(atan2(dy, dx))
	if rawAngle < 0: 
		rawAngle += 180
	theta = int(((abs(rawAngle + 22.5) // 45)) % 4)
	return theta

def cannyTransform(image):
	image2 = [0] * WIDTH * HEIGHT
	pos = [-1, +1, -WIDTH+1, WIDTH - 1, -WIDTH, WIDTH, -WIDTH-1, WIDTH+1]
	for row in range(1, HEIGHT-1):
		for col in range(1, WIDTH-1):
			index = col+row*HEIGHT		
			deg = image[index][1]
			cellsLookedAt = (index+pos[2*deg], index+pos[2*deg+1])
			if (image[index][0] > image[cellsLookedAt[0]][0]) and (image[index][0] > image[cellsLookedAt[1]][0]):
				image[index][2] = 1
	image2 = doubleThreshold(image)
	return image2

def doubleThreshold(image):
	for e in range(len(image)):
		if image[e][2] == 0:
			continue
		elif (image[e][2] == 1) and (image[e][0] > HIGH):
			image[e][4] = 1
			checkNeighbors(e, image)
	return image

def checkNeighbors(e, image):
	cellsToVisit = [e - WIDTH, e - 1, e + 1, e + WIDTH]		#[N, W, E, S]
	if image[e][3] == 1:
		return
	image[e][3] = 1
	for index in cellsToVisit:
		if (index > 0) and (image[index][2] == 1) and (image[index][0] > LOW) and (index < WIDTH * HEIGHT):
			image[index][3] = 1
			if image[index][4] == 0:
				image[index][4] = 1
				checkNeighbors(index, image)

def normalize(image, intensity = 255):
	m = max(image)
	printElapsedTime('normalizing')
	return [int(x*intensity/m) for x in image]

def printElapsedTime(msg = 'time'):
	length = 30
	msg = msg[:length]
	tab = '.'*(length-len(msg))
	print('--' + msg.upper() + tab + ' ', end = "")
	time = round(clock()-START, 1)
	print('%2d'%int(time/60), ' min :', '%4.1f'%round(time%60, 1), \
		' sec', sep = '')

def displayImageInWindow(image):
	global x
	x = ImageFrame(image)

class ImageFrame:
	def __init__(self, image, COLORFLAG = False):
		self.img = PhotoImage(width = WIDTH, height = HEIGHT)
		for row in range(HEIGHT): 
			for col in range(WIDTH):
				num = image[row*WIDTH + col]
				if COLORFLAG == True:
					kolor = '#%02x%02x%02x' % (num[0], num[1], num[2])
				else:
					kolor = '#%02x%02x%02x' % (num, num, num)
				self.img.put(kolor, (col, row))
		c = Canvas(root, width = WIDTH, height = HEIGHT); c.pack()
		c.create_image(0, 0, image = self.img, anchor = NW)
		printElapsedTime('displayed image')

if __name__ == '__main__': main()
