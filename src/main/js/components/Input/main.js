import { useState } from "react";
import React, { useContext } from 'react';
const Input = (props) => {
    const { value, bind, reset } = useInput(props.state.tokenId)
    const tokenContext = (evt) => {
        React.createContext({ tokenId: evt.target.value });    
    };
    const startContext = (evt) => {
        React.createContext({ startDate: evt.target.value });    
    };
    const endContext = (evt) => {
        React.createContext({ endDate: evt.target.value });    
    };
     
    return(
        <div>
            <input name="token" type="text" onChange={tokenContext()}></input>
            <input name="from" type="date" onChange={startContext()}></input>
            <input name="to" type="date" onChange={endContext()}></input>
        </div>      
        
        )
    }
    
     const useInput = (initialValue) => {
        const [value, setValue] = useState(initialValue);
        
        return {
            value,
            setValue,
            reset: () => setValue(""),
            bind: {
                value,
                onChange: event => {
                    setValue(event.target.value);
                }
            }
        };
    };

    export default Input;