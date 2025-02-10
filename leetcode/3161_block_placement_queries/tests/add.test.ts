import {isBlockedByQuerie} from '../src/solution';


it('example 1 should be success', () => {
    
    const queries = [[1,2],[2,3,3],[2,3,1],[2,2,2]]
    const result  = [false,true,true];
    
    expect(isBlockedByQuerie(queries)).toEqual(result);
})

it('example 2 should be success', () => {
    
    const queries = [[1,7],[2,7,6],[1,2],[2,7,5],[2,7,6]]
    const result  = [true,true,false];
    
    expect(isBlockedByQuerie(queries)).toEqual(result);
})
