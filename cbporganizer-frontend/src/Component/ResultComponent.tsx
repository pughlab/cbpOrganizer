import React, {useContext} from "react";
import SharedDataContext from "../service/SharedDataContext";

const ResultComponent = () => {
    const { validationResult, setValidationResult } = useContext(SharedDataContext);

    return (
        <>
            {validationResult && (
                <iframe src={validationResult} title="HTML Content" width="100%"
                        height="100%" />
            )}
        </>
    );
}

export default ResultComponent;

