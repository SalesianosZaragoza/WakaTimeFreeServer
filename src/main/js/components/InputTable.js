import React, { useContext } from 'react';
import { UPDATE_TOKEN, UPDATE_FROM, UPDATE_TO, store} from "../StateProvider"
const Input = (props) => {
    const context = useContext(store);
    const payload ={
        tokenId:"",
        from:"",
        to:""
    }
    const setTokenId = (value) => {
        payload.tokenId = value;
        context.dispatch({ type: UPDATE_TOKEN, payload: payload});
    }
    const setDateFrom = (value) => {
        payload.from = value;
        context.dispatch({ type: UPDATE_FROM, payload: payload});
    }
    const setDateTo = (value) => {
        payload.to = value;
        context.dispatch({ type: UPDATE_TO, payload: payload});
    }
    return(
        <div>
            <input name="token" type="text" onChange={e => setTokenId(e.target.value)}></input>
            <input name="from" type="date" onChange={e => setDateFrom(e.target.value)}></input>
            <input name="to" type="date" onChange={e => setDateTo(e.target.value)}></input>
        </div>      
        )
    }
    
    
    export default Input;