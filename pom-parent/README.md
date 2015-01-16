FrontlineSMS parent-pom - Maven Parent Project
==============================================

This project defines a common parent POM for FrontlineSMS core and plugin projects to allow easy inclusion of common tools and standard versions of shared libraries.  Top inherit from this POM, add the following to your project's POM:

	<project ...>
		...
		<parent>
			<groupId>net.frontlinesms.core</groupId>
			<artifactId>parent-pom</artifactId>
			<version>${coreVersion}</version>
			<relativePath>../parent-pom</relativePath>
		</parent>
		...
	</project>
**Where *${coreVersion}* is the version of the FrontlineSMS core you would like to use.**
