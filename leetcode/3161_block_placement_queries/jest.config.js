const { transform } = require("typescript");

module.exports = { 
    preset:"ts-jest",
    testEnvironment: "node",
    roots: ["<rootDir>/tests"],
    transform: {
        "ˆ.+\\.tsx?$":"ts-jest",
    },
    testRegex:"((\\.|/)(test|spec))\\.tsx?$",
    moduleFileExtensions: ["ts","tsx","js","jsx","json","node"],
};