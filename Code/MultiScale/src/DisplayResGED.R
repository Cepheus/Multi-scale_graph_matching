#jpeg(file = "/Users/romainraveaux/Documents/svn/devrecherche//BipartiteGraphMatchingEditDistance/data/result/myplot%d.jpeg")
png(file = "/Users/romainraveaux/Documents/svn/devrecherche//BipartiteGraphMatchingEditDistance/data/result/myplot%d.png")
#bg = "transparent"

data <- as.matrix(read.csv("/Users/romainraveaux/Documents/svn/devrecherche//BipartiteGraphMatchingEditDistance/data/result/resultperfletter.csv",header=TRUE,sep=";"))

col<-5
nbgraph<-as.numeric(data[1,1]);
nbpairs<-nbgraph*nbgraph
offsetavant<-(1+nbpairs);
distanceGEDPLAin<-data[2:offsetavant,col]
	
#################################	
#while(TRUE == TRUE){
	offsetapres<-(offsetavant+nbpairs);
	distanceGEDAstarNoassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#}

#################################
	offsetapres<-(offsetavant+nbpairs);
	distanceGEDAstarMunkresassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################
	offsetapres<-(offsetavant+nbpairs);
	distanceGEDAstarLAPassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;

#################################

	offsetapres<-(offsetavant+nbpairs);
	distanceGEDBeamSearch1<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;

#################################
	offsetapres<-(offsetavant+nbpairs);
	distanceGEDBeamSearch10<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#################################
	offsetapres<-(offsetavant+nbpairs);
	distanceGEDBeamSearch100<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#################################
	offsetapres<-(offsetavant+nbpairs);
	distanceGEDMunkres<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
#################################
	offsetapres<-(offsetavant+nbpairs);
	distanceGEDLowerUpperBounds<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
	if(as.numeric(distanceGEDPLAin[1])==-1) {
		distanceGEDPLAin<-distanceGEDAstarNoassingment;
 	}
 	
 		if(as.numeric(distanceGEDAstarNoassingment[1])==-1) {
		distanceGEDPLAin<-distanceGEDAstarMunkresassingment;
 	}
	ymax<-as.numeric(c(max(distanceGEDPLAin),max(distanceGEDAstarNoassingment)));
	ymax<-as.matrix(ymax)
	xmin<-as.numeric(c(-1,-1));
	xmin<-as.matrix(xmin);
	
	plot(distanceGEDPLAin,distanceGEDAstarNoassingment);
	segments(xmin[1,1],xmin[2,1],ymax[1,1]+1000,ymax[2,1]+1000)
	
	
	plot(distanceGEDPLAin,distanceGEDAstarMunkresassingment);
	segments(xmin[1,1],xmin[2,1],ymax[1,1]+1000,ymax[2,1]+1000)
	
	plot(distanceGEDPLAin,distanceGEDAstarLAPassingment);
	segments(xmin[1,1],xmin[2,1],ymax[1,1]+1000,ymax[2,1]+1000)
	
	plot(distanceGEDPLAin,distanceGEDBeamSearch1);
	segments(xmin[1,1],xmin[2,1],ymax[1,1]+1000,ymax[2,1]+1000)
	
	plot(distanceGEDPLAin,distanceGEDBeamSearch10);
	segments(xmin[1,1],xmin[2,1],ymax[1,1]+1000,ymax[2,1]+1000)
	
	plot(distanceGEDPLAin,distanceGEDBeamSearch100);
	segments(xmin[1,1],xmin[2,1],ymax[1,1]+1000,ymax[2,1]+1000)
	
	plot(distanceGEDPLAin,distanceGEDMunkres);
	segments(xmin[1,1],xmin[2,1],ymax[1,1]+1000,ymax[2,1]+1000)
	
	plot(distanceGEDPLAin,distanceGEDLowerUpperBounds);
	segments(xmin[1,1],xmin[2,1],ymax[1,1]+1000,ymax[2,1]+1000)
	
#################################
#################################
#################################
#################################
##NB noeuds exploré
#################################
#################################
#################################
#################################


offsetavant<-(1+nbpairs);
col<-6;
ExploredNodesGEDPLAin<-data[2:offsetavant,col]
	
#################################	
	offsetapres<-(offsetavant+nbpairs);
	ExploredNodesGEDAstarNoassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	

#################################
	offsetapres<-(offsetavant+nbpairs);
	ExploredNodesGEDAstarMunkresassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################
	offsetapres<-(offsetavant+nbpairs);
	ExploredNodesGEDAstarLAPassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################

	offsetapres<-(offsetavant+nbpairs);
	ExploredNodesGEDBeamSearch1<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################
	offsetapres<-(offsetavant+nbpairs);
	ExploredNodesGEDBeamSearch10<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	offsetapres<-(offsetavant+nbpairs);
	ExploredNodesGEDBeamSearch100<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	offsetapres<-(offsetavant+nbpairs);
	ExploredNodesGEDLowerUpperBounds<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	offsetapres<-(offsetavant+nbpairs);
	ExploredNodesGEDLowerUpperBounds<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	
	if(as.numeric(ExploredNodesGEDPLAin[1])==-1) {
		ExploredNodesGEDPLAin<-ExploredNodesGEDAstarNoassingment;
 	}
		
plot(ExploredNodesGEDPLAin,ExploredNodesGEDAstarNoassingment);
plot(ExploredNodesGEDPLAin,ExploredNodesGEDAstarMunkresassingment);
	plot(ExploredNodesGEDPLAin,ExploredNodesGEDAstarLAPassingment);
	plot(ExploredNodesGEDPLAin,ExploredNodesGEDBeamSearch1);
	plot(ExploredNodesGEDPLAin,ExploredNodesGEDBeamSearch10);
	plot(ExploredNodesGEDPLAin,ExploredNodesGEDBeamSearch100);
	plot(ExploredNodesGEDPLAin,ExploredNodesGEDLowerUpperBounds);
	
ExploredNodesGEDPLAinmean<-mean(as.numeric(ExploredNodesGEDPLAin))
ExploredNodesGEDAstarNoassingmentmean<-mean(as.numeric(ExploredNodesGEDAstarNoassingment))
ExploredNodesGEDAstarMunkresassingmentmean<-mean(as.numeric(ExploredNodesGEDAstarMunkresassingment))
ExploredNodesGEDAstarLAPassingmentmean<-mean(as.numeric(ExploredNodesGEDAstarLAPassingment))
ExploredNodesGEDBeamSearch1mean<-mean(as.numeric(ExploredNodesGEDBeamSearch1))
ExploredNodesGEDBeamSearch10mean<-mean(as.numeric(ExploredNodesGEDBeamSearch10))
ExploredNodesGEDBeamSearch100mean<-mean(as.numeric(ExploredNodesGEDBeamSearch100))
ExploredNodesGEDLowerUpperBoundsmean<-mean(as.numeric(ExploredNodesGEDLowerUpperBounds))

vec<-c(ExploredNodesGEDPLAinmean,ExploredNodesGEDAstarNoassingmentmean,ExploredNodesGEDAstarMunkresassingmentmean,ExploredNodesGEDAstarLAPassingmentmean,ExploredNodesGEDBeamSearch1mean,ExploredNodesGEDBeamSearch10mean,ExploredNodesGEDBeamSearch100mean,ExploredNodesGEDLowerUpperBoundsmean)

leg<-c("ExploredNodesGEDPLAinmean", "ExploredNodesGEDAstarNoassingmentmean", "ExploredNodesGEDAstarMunkresassingmentmean","ExploredNodesGEDAstarLAPassingmentmean","ExploredNodesGEDBeamSearch1mean","ExploredNodesGEDBeamSearch10mean","ExploredNodesGEDBeamSearch100mean","ExploredNodesGEDLowerUpperBoundsmean");
par(mar=c(13.1, 2.1, 1.1, 2.1), xpd=TRUE)

barplot(vec,col = rainbow(12))
legend("bottomright", inset=c(0.2,-0.55), legend=leg, title="Legend",col=rainbow(12),pch=15)

#legend('bottomleft',leg,
#         col=rainbow(12))

# # setup for no margins on the legend
# par(mar=c(0, 0, 0, 0))

 # plot.new()
 # legend('center','groups',leg,
        # col=rainbow(12),bty ="n")



# legend("topright", inset=c(-0.2,0), legend=leg, pch=c(1,3), title="Group")




# this legend gets clipped:
 #legend(-1,30,leg, col = rainbow(12))

 # so turn off clipping:
 # par(xpd=TRUE)
 # legend(2.8,-1,leg, plty = c(1,2))



#################################
#################################
#################################
#################################
##NB max size open
#################################
#################################
#################################
#################################


offsetavant<-(1+nbpairs);
col<-7;
MaxOpenSizeGEDPLAin<-data[2:offsetavant,col]
	
#################################	
	offsetapres<-(offsetavant+nbpairs);
	MaxOpenSizeGEDAstarNoassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	

#################################
	offsetapres<-(offsetavant+nbpairs);
	MaxOpenSizeGEDAstarMunkresassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################
	offsetapres<-(offsetavant+nbpairs);
	MaxOpenSizeGEDAstarLAPassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################

	offsetapres<-(offsetavant+nbpairs);
	MaxOpenSizeGEDBeamSearch1<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################
	offsetapres<-(offsetavant+nbpairs);
	MaxOpenSizeGEDBeamSearch10<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	offsetapres<-(offsetavant+nbpairs);
	MaxOpenSizeGEDBeamSearch100<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	offsetapres<-(offsetavant+nbpairs);
	MaxOpenSizeGEDLowerUpperBounds<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	offsetapres<-(offsetavant+nbpairs);
	MaxOpenSizeGEDLowerUpperBounds<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	
if(as.numeric(MaxOpenSizeGEDPLAin[1])==-1) {
		MaxOpenSizeGEDPLAin<-MaxOpenSizeGEDAstarNoassingment;
 	}
	
plot(MaxOpenSizeGEDPLAin,MaxOpenSizeGEDAstarNoassingment);

	plot(MaxOpenSizeGEDPLAin,MaxOpenSizeGEDAstarMunkresassingment);
	plot(MaxOpenSizeGEDPLAin,MaxOpenSizeGEDAstarLAPassingment);
	plot(MaxOpenSizeGEDPLAin,MaxOpenSizeGEDBeamSearch1);
	plot(MaxOpenSizeGEDPLAin,MaxOpenSizeGEDBeamSearch10);
	plot(MaxOpenSizeGEDPLAin,MaxOpenSizeGEDBeamSearch100);
	plot(MaxOpenSizeGEDPLAin,MaxOpenSizeGEDLowerUpperBounds);
	
	MaxOpenSizeGEDPLAinmean<-mean(as.numeric(MaxOpenSizeGEDPLAin))
MaxOpenSizeGEDAstarNoassingmentmean<-mean(as.numeric(MaxOpenSizeGEDAstarNoassingment))
MaxOpenSizeGEDAstarMunkresassingmentmean<-mean(as.numeric(MaxOpenSizeGEDAstarMunkresassingment))
MaxOpenSizeGEDAstarLAPassingmentmean<-mean(as.numeric(MaxOpenSizeGEDAstarLAPassingment))
MaxOpenSizeGEDBeamSearch1mean<-mean(as.numeric(MaxOpenSizeGEDBeamSearch1))
MaxOpenSizeGEDBeamSearch10mean<-mean(as.numeric(MaxOpenSizeGEDBeamSearch10))
MaxOpenSizeGEDBeamSearch100mean<-mean(as.numeric(MaxOpenSizeGEDBeamSearch100))
MaxOpenSizeGEDLowerUpperBoundsmean<-mean(as.numeric(MaxOpenSizeGEDLowerUpperBounds))

vec<-c(MaxOpenSizeGEDPLAinmean,MaxOpenSizeGEDAstarNoassingmentmean,MaxOpenSizeGEDAstarMunkresassingmentmean,MaxOpenSizeGEDAstarLAPassingmentmean,MaxOpenSizeGEDBeamSearch1mean,MaxOpenSizeGEDBeamSearch10mean,MaxOpenSizeGEDBeamSearch100mean,MaxOpenSizeGEDLowerUpperBoundsmean)
#barplot(vec, col = rainbow(12),legend=c("MaxOpenSizeGEDPLAinmean", "MaxOpenSizeGEDAstarNoassingmentmean", "MaxOpenSizeGEDAstarMunkresassingmentmean","MaxOpenSizeGEDAstarLAPassingmentmean","MaxOpenSizeGEDBeamSearch10mean","MaxOpenSizeGEDBeamSearch100mean","MaxOpenSizeGEDBeamSearch1000mean","MaxOpenSizeGEDLowerUpperBoundsmean"))

leg<-c("MaxOpenSizeGEDPLAinmean", "MaxOpenSizeGEDAstarNoassingmentmean", "MaxOpenSizeGEDAstarMunkresassingmentmean","MaxOpenSizeGEDAstarLAPassingmentmean","MaxOpenSizeGEDBeamSearch1mean","MaxOpenSizeGEDBeamSearch10mean","MaxOpenSizeGEDBeamSearch100mean","MaxOpenSizeGEDLowerUpperBoundsmean");
par(mar=c(13.1, 2.1, 1.1, 2.1), xpd=TRUE)

barplot(vec,col = rainbow(12))
legend("bottomright", inset=c(0.2,-0.55), legend=leg, title="Legend",col=rainbow(12),pch=15)


#################################
#################################
#################################
#################################
##TIME
#################################
#################################
#################################
#################################



offsetavant<-(1+nbpairs);
col<-8;
timeGEDPLAin<-data[2:offsetavant,col]
	
#################################	
	offsetapres<-(offsetavant+nbpairs);
	timeGEDAstarNoassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	

#################################
	offsetapres<-(offsetavant+nbpairs);
	timeGEDAstarMunkresassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################
	offsetapres<-(offsetavant+nbpairs);
	timeGEDAstarLAPassingment<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################

	offsetapres<-(offsetavant+nbpairs);
	timeGEDBeamSearch1<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
#################################
	offsetapres<-(offsetavant+nbpairs);
	timeGEDBeamSearch10<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	offsetapres<-(offsetavant+nbpairs);
	timeGEDBeamSearch100<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	offsetapres<-(offsetavant+nbpairs);
	timeGEDMunkres<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	#############################
	offsetapres<-(offsetavant+nbpairs);
	timeGEDLowerUpperBounds<-data[(1+offsetavant):offsetapres,col]
	offsetavant<-offsetapres;
	
	if(as.numeric(timeGEDPLAin[1])==-1) {
		timeGEDPLAin<-timeGEDAstarNoassingment;
 	}
	
plot(timeGEDPLAin,timeGEDAstarNoassingment);

	plot(timeGEDPLAin,timeGEDAstarMunkresassingment);
	plot(timeGEDPLAin,timeGEDAstarLAPassingment);
	plot(timeGEDPLAin,timeGEDBeamSearch1);
	plot(timeGEDPLAin,timeGEDBeamSearch10);
	plot(timeGEDPLAin,timeGEDBeamSearch100);
	plot(timeGEDPLAin,timeGEDMunkres);
	plot(timeGEDPLAin,timeGEDLowerUpperBounds);
	
	
	timeGEDPLAinmean<-mean(as.numeric(timeGEDPLAin))
	
timeGEDAstarNoassingmentmean<-mean(as.numeric(timeGEDAstarNoassingment))

timeGEDAstarMunkresassingmentmean<-mean(as.numeric(timeGEDAstarMunkresassingment))

timeGEDAstarLAPassingmentmean<-mean(as.numeric(timeGEDAstarLAPassingment))

timeGEDBeamSearch1mean<-mean(as.numeric(timeGEDBeamSearch1))
timeGEDBeamSearch10mean<-mean(as.numeric(timeGEDBeamSearch10))
timeGEDBeamSearch100mean<-mean(as.numeric(timeGEDBeamSearch100))
timeGEDMunkresmean<-mean(as.numeric(timeGEDMunkres))
timeGEDLowerUpperBoundsmean<-mean(as.numeric(timeGEDLowerUpperBounds))


vec<-c(timeGEDPLAinmean,timeGEDAstarNoassingmentmean,timeGEDAstarMunkresassingmentmean,timeGEDAstarLAPassingmentmean,timeGEDBeamSearch1mean,timeGEDBeamSearch10mean,timeGEDBeamSearch100mean,timeGEDMunkresmean,timeGEDLowerUpperBoundsmean)
#barplot(vec, col = rainbow(12),legend=c("timeGEDPLAinmean", "timeGEDAstarNoassingmentmean", "timeGEDAstarMunkresassingmentmean","timeGEDAstarLAPassingmentmean","timeGEDBeamSearch10mean","timeGEDBeamSearch100mean","timeGEDBeamSearch1000mean","timeGEDMunkresmean","timeGEDLowerUpperBoundsmean"))

leg<-c("timeGEDPLAinmean", "timeGEDAstarNoassingmentmean", "timeGEDAstarMunkresassingmentmean","timeGEDAstarLAPassingmentmean","timeGEDBeamSearch10mean","timeGEDBeamSearch100mean","timeGEDBeamSearch1000mean","timeGEDMunkresmean","timeGEDLowerUpperBoundsmean")
par(mar=c(13.1, 2.1, 1.1, 2.1), xpd=TRUE)

barplot(vec,col = rainbow(12))
legend("bottomright", inset=c(0.2,-0.6), legend=leg, title="Legend",col=rainbow(12),pch=15)

dev.off()

a<-which(distanceGEDAstarMunkresassingment>distanceGEDPLAin)
length(which(distanceGEDAstarMunkresassingment>distanceGEDPLAin))
length(which(distanceGEDAstarLAPassingment>distanceGEDPLAin))
data[1+a,1:5]
distanceGEDAstarMunkresassingment[a[1]]
distanceGEDPLAin[a[1]]



ExploredNodesGEDAstarNoassingmentmean
ExploredNodesGEDAstarMunkresassingmentmean
ExploredNodesGEDAstarLAPassingmentmean
ExploredNodesGEDBeamSearch1mean
ExploredNodesGEDBeamSearch10mean
ExploredNodesGEDBeamSearch100mean
ExploredNodesGEDLowerUpperBoundsmean


MaxOpenSizeGEDAstarNoassingmentmean
MaxOpenSizeGEDAstarMunkresassingmentmean
MaxOpenSizeGEDAstarLAPassingmentmean
MaxOpenSizeGEDBeamSearch1mean
MaxOpenSizeGEDBeamSearch10mean
MaxOpenSizeGEDBeamSearch100mean
MaxOpenSizeGEDLowerUpperBoundsmean



timeGEDAstarMunkresassingmentmean
timeGEDAstarLAPassingmentmean
timeGEDBeamSearch1mean
timeGEDBeamSearch10mean
timeGEDBeamSearch100mean
timeGEDMunkresmean
timeGEDLowerUpperBoundsmean
