Ext.data.Store.prototype.createComparator = function(sorters){  
    return function(r1, r2){  
        var s = sorters[0], f=s.property;  
        var v1 = r1.data[f], v2 = r2.data[f];  
          
        var result = 0;  
        if(typeof(v1) == "string"){  
            result = v1.localeCompare(v2);  
            if(s.direction == 'DESC'){  
                result *=-1;  
            }  
        } else {  
            result =sorters[0].sort(r1, r2);  
        }  
          
        var length = sorters.length;  
          
        for(var i = 1; i<length; i ++){  
            s = sorters[i];  
            f = s.property;  
            v1 = r1.data[f];  
            v2 = r2.data[f];  
            if(typeof(v1) == "string"){  
                result = result || v1.localeCompare(v2);  
                if(s.direction == 'DESC'){  
                    result *=-1;  
                }  
            } else {  
                result = result || s.sort.call(this, r1, r2);  
            }  
        }  
        return result;  
    };  
};  