# github-issue-export (0.2.0 - 2017-07-18)

Simple propject to export github issues to a xls spreadsheet.

## Quickstart

### Clone (use master or develop branch)
git clone https://github.com/fugerit79/github-issue-export.git

### Build
From base dir : 
mvn clean install -P singlepackage

### Run
java -jar target/dist-github-issue-export-0.1.0-SNAPSHOT.jar --owner fugerit79 --repo github-issue-export --lang it --xls-file target/report.xls


## TODO
Option to select the field to export in spreadsheet format
