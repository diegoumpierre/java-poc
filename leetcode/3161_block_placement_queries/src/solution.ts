
export function isBlockedByQuerie(queries: number[][]){
    
    var result: boolean[]=[];
    var line: boolean[]=[];
    
    var query: number[]=[];
    var posResult: number = 0;
    for (let i = 0; i < queries.length; i++) {
        query = queries[i];
        if (query[0] == 1){
            line[query[1]] = true;
           
        }
        if (query[0] == 2){
            result[posResult] = existBlock(query[1],query[2], line);
            posResult +=1;
        }
    }

    console.log(result);
    return result;
}

function existBlock(pos: number, size: number, line: boolean[]){

    var freeSpace = 0;
   
    for (let i = 0; i <= pos; i++) {
        if(line[i] == false || line[i] == undefined){
            freeSpace +=1;
        }else{
            freeSpace = 1;
        }
        if (freeSpace >= size){
            return true;
        }
    }
   
    return false;
}