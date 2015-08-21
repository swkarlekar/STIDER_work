LENGTH = 10000	
HEIGHT = 1000
WIDTH = 1000
cpoints = []
oldcpoints = []
data = []
kolors = ["BLACK", "BLUE", "RED", "GREEN", "YELLOW", "PURPLE", "MAGENTA", "ORANGE", "WHITE"]
from random import random
import matplotlib.pyplot
from tkinter import *
from time import clock
from math import sin, cos, tan, atan2, pi, sqrt, trunc
import pdb
CLUSTERS = 3

def createDataPoints(image):
	covnum = int(random()*400+100)
	mean1 = [int(random()*600+400) for x in range(2)]
	
	#mean = [500,450]
	cov = [[covnum,0],[0,covnum]]
	createACluster(mean1, cov, 50, image)
	
	covnum = int(random()*400+100)
	mean1 = [int(random()*600+400) for x in range(2)]

	#mean = [400,480]
	cov = [[400,0],[0,400]]
	createACluster(mean1, cov, 50, image)

	covnum = int(random()*400+100)
	mean1 = [int(random()*600+400) for x in range(2)]

	#mean = [400,480]
	cov = [[400,0],[0,400]]
	createACluster(mean1, cov, 50, image)

def plot(x, y, image, term = 1):
	maxIndex = len(image)
	index = int(y)*WIDTH+int(x)
	#print(x, y, index)
	image[index] = term

def createImage():
	image = [0]*(HEIGHT * WIDTH)
	return image

def findNewCentroids(clusters):
	lstx = []
	lsty = []
	global oldcpoints
	oldcpoints = cpoints
	global cpoints
	cpoints = []
	for cluster in clusters:
		for coordinates in cluster: 
			lstx.append(coordinates[0])
			lsty.append(coordinates[1])
		newx = trunc(sum(lstx)/len(lstx)*1000)/1000
		newy = trunc(sum(lsty)/len(lsty)*1000)/1000
		cpoints.append([newx, newy])

def createEmptyClusters(): 
	return [[] for x in range(CLUSTERS)]

def createACluster(mean, cov, points, image):
	import numpy as np
	import matplotlib.pyplot as plt
	global data
	x,y = np.random.multivariate_normal(mean,cov,points).T
	for i in range(len(x)):
		plot(int(x[i]), int(y[i]), image)
		data.append([int(x[i]), int(y[i])])
	
def pickClusterPoints(clusters, image): 
	global cpoints
	cpoints =  [data[int(random()*len(data))] for x in range(CLUSTERS)]
	for i in range(CLUSTERS): 
		plot(cpoints[i][0], cpoints[i][1], image, 2)
		clusters[i].append(cpoints[i])
		

def linkToClosestCluster(element, clusters): 
	min = HEIGHT*sqrt(2)
	indexCluster = 0
	for i in range(len(cpoints)): 
		temp = findDistance(element[0], element[1], cpoints[i][0], cpoints[i][1])
		if min > temp: 
			min = temp
			indexCluster = i
	clusters[indexCluster].append(element)
	return indexCluster

def assignColorsToClusters(clusters, image): 
	clusterindex = 1
	for cluster in clusters:
		print("length of clusters", len(clusters), "clusterIndex", clusterindex)
		for coordinate in cluster: 
			plot(coordinate[0], coordinate[1], image, clusterindex)
		clusterindex +=1

def findDistance(x1, y1, x2, y2): 
	return sqrt((x2-x1)**2 + (y2-y1)**2)

def displayImageInWindow(image, flag = True):
    	global x
    	if flag: x = ImageFrame(image)
    	root.mainloop()

def clusterPoints(): 
	iterations = 0
	pickClusterPoints(clusters, image)
	while oldcpoints != cpoints: 
		print(iterations, "iteration is starting", oldcpoints, cpoints)
		iterations +=1
		for element in data:
			linkToClosestCluster(element, clusters)
		findNewCentroids(clusters)
	assignColorsToClusters(clusters, image)

class ImageFrame:
	def __init__(self, image):
		self.img = PhotoImage(width = WIDTH, height = HEIGHT)
		for row in range(HEIGHT): 
			for col in range(WIDTH):
				index = int(row)*WIDTH+int(col)
				self.img.put(kolors[int(image[index])], (col, row))
		c = Canvas(root, width = WIDTH, height = HEIGHT); c.pack()
		c.create_image(0, 0, image = self.img, anchor = NW)

if __name__ == "__main__": 
	root = Tk()
	image = createImage()
	clusters = createEmptyClusters()
	createDataPoints(image)
	clusterPoints()
	print("\nData processing is complete. Waiting for image to load...")
	displayImageInWindow(image)

	
#Twitter API Keys
#Consumer Secret (API Secret):  RbUUE8j6xBUZGuoOloUovUWJDGnTaaIXUWyFNXx6Bw5KmEjKYy
#Consumer Key (API Key) DBds2BSKBzppbfkYAX3eksQdC 
#numpy random multivariate normal 
#lst = [[int(random()*500), int(random()*500)] for x in range(LENGTH)]
