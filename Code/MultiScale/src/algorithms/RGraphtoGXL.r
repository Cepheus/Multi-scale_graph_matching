library("igraph")
loop<-5
while(loop<=50)
{	
	dir.create(paste(c('C:/SG-',loop), collapse=''))
	gxlFolder  <- paste(c('C:/SG-',loop), collapse='')
	print("**************gxlFolder")
	print(gxlFolder)
	probability <- 0.0;
	for(k in 1:50)
	{
		
		probability <- 0.40
		g <- erdos.renyi.game(loop, probability, type=c("gnp"))
		gxlFile <- paste(c(gxlFolder,'/',g[1],'-',k,'.gxl'), collapse='')
		print("***********************************gxlFile")	
		print(gxlFile)
		gxlId <- paste(c(g[1],'-',k), collapse='')


		if(g[2]==FALSE)
		{
			edgemode<-"undirected";
		}
		if(g[2]==TRUE){
			edgemode <- "directed";
		}
		edgeId <-"false"
			
		nodes1 <- toString(g[3])
		nodes1 <- unlist(strsplit(nodes1, "[ ,]"))
		nodes1 <- unlist(strsplit(nodes1, "[c(]"))
		nodes1 <- unlist(strsplit(nodes1, "[)]"))

		nodes2 <- toString(g[4])
		nodes2 <- unlist(strsplit(nodes2, "[ ,]"))
		nodes2 <- unlist(strsplit(nodes2, "[c(]"))
		nodes2 <- unlist(strsplit(nodes2, "[)]"))

		nodesList <- ""

		write(paste(c('<?xml version="1.0" encoding="UTF-8"?>'), collapse=''), gxlFile)
		write(paste(c('<!DOCTYPE gxl SYSTEM "http://www.gupro.de/GXL/gxl-1.0.dtd">'), collapse=''), gxlFile, append=TRUE)

		write(paste(c('<gxl xmlns:xlink="http://www.w3.org/1999/xlink">'), collapse=''), gxlFile, append=TRUE )
		write(paste(c('<graph id="',gxlId,'" edgeids="',edgeId,'" edgemode="',edgemode,'">'), collapse=''), gxlFile,append=TRUE)

		nodesSize <-  as.numeric(g[1])
		for(i in 1:nodesSize)
		{

			write(paste(c('<node id="',i-1,'">'), collapse=''), gxlFile,append=TRUE)
				 
			write(paste(c('<attr name="x">'), collapse=''), gxlFile,append=TRUE)
			write(paste(c('<float>1.000000</float>'), collapse=''), gxlFile,append=TRUE)
			write(paste(c('</attr>'), collapse=''), gxlFile,append=TRUE)
				
			write(paste(c('<attr name="y">'), collapse=''), gxlFile,append=TRUE)
			write(paste(c('<float>1.000000</float>'), collapse=''), gxlFile,append=TRUE)
			write(paste(c('</attr>'), collapse=''), gxlFile,append=TRUE)

			write("</node>", gxlFile,append=TRUE)

			
		}


		for(i in 1:length(nodes1))
		{
			write(paste(c('<edge from="',nodes1[i],'" to="',nodes2[i],'"/>'), collapse=''), gxlFile,append=TRUE)
			
		}
		write("</graph>", gxlFile,append=TRUE)
		write("</gxl>", gxlFile,append=TRUE)


		print("FINISHED")
		print(k)
		print(loop)
	}
	loop<- loop+5;
}
