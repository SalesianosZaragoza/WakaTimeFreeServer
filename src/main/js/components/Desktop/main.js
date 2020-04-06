import PieChart from "../PieChart/main"
import Input from "../Input/main"
import React, { useContext } from 'react';
import { store } from "../../StateProvider"
const Categories = { BRANCH : "branch",  PROJECT : "project", LANGUAGE : "language", FILENAME : "filename"}
Object.freeze(Categories)
const Desktop = (props) => {
    const state = useContext(store);  
    return (
        <>
            <PieChart title={Categories.LANGUAGE} query={state.query(Categories.LANGUAGE)} />
            <PieChart title={Categories.FILENAME} query={state.query(Categories.FILENAME)}/>
            <PieChart title={Categories.BRANCH} query={state.query(Categories.BRANCH)}/>
            <PieChart title={Categories.PROJECT} query={state.query(Categories.PROJECT)}/>
            <Input/>
        </>
        );
    }
    export default Desktop;
    