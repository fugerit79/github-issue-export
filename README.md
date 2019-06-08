# github-issue-export

Simple propject to export github issues to a xls spreadsheet.

## Quickstart

### Clone (use master or develop branch for latest snapshot, release/x.x.x branch for stable versions)
git clone https://github.com/fugerit79/github-issue-export.git

### Build
From base dir : 
mvn clean install -P singlepackage

### Run with a simple gui
java -jar target/dist-github-issue-export-X.X.X.jar

### Run on command line
java -jar target/dist-github-issue-export-X.X.X.jar --gui 0 --owner fugerit79 --repo github-issue-export --lang it --xls-file target/report.xls


## CHANGELOG
[https://github.com/fugerit79/github-issue-export/blob/master/CHANGE_LOG.md](https://github.com/fugerit79/github-issue-export/blob/master/CHANGE_LOG.md)
