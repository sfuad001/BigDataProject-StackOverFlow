const path = require('path');
const fs = require('fs');
const { json } = require('express');

function readCSV (filepath) {
    const csvData = fs.readFileSync(filepath, {encoding:'utf8', flag:'r'});
    console.log
    return csvData;
}

function convertCSVtoJson (csvData) {
    csvData = csvData.replace(/\r\n/g, '\n');
    const lines = csvData.toString().split('\n');
    let columnNames;
    const jsonList = [];
    if (lines.length > 0) {
        columnNames = lines[0].split(",");
    }

    for (let i = 1; i < lines.length; i++) {
        const data = {};
        const lineDataList = lines[i].split(",");
        for (let j = 0; j < columnNames.length; j++) {
            data[columnNames[j]] = lineDataList[j];
        }
        jsonList.push(data);
    }

    return jsonList;
}

module.exports.readCSV = readCSV;
module.exports.convertCSVtoJson = convertCSVtoJson;