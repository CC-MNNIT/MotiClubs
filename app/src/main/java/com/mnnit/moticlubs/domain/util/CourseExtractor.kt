package com.mnnit.moticlubs.domain.util

object CourseExtractor {

    data class Course(val stream: String, val branch: String)

    // Regex for extracting course identifier from regNo
    private val COURSE_CODE_REGEX = "(?<=[0-9]{4})([0-9](?=[0-9]{3})|[a-zA-Z]+(?=[0-9]{2}))".toRegex()
    private const val UNIDENTIFIED = "N/A"

    private val COURSE_MAP = HashMap<String, String>().apply {
        // B.Tech.
        this["0"] = "Biotechnology"
        this["1"] = "Civil Engineering"
        this["2"] = "Electrical Engineering"
        this["3"] = "Mechanical Engineering"
        this["4"] = "Computer Science and Engineering"
        this["5"] = "Electronics and Communication Engineering"
        this["6"] = "Production and Industrial Engineering"
        this["8"] = "Information Technology"
        this["9"] = "Chemical Engineering"

        // MBA
        this["CA"] = "Master of Computer Application"

        // MCA
        this["MB"] = "Master of Business Administration"

        // M.Sc.
        this["MSC"] = "Mathematics And Scientific Computing"

        // M.Tech.
        this["AM"] = "Applied Mechanics"
        this["BM"] = "Biomedical Engineering"
        this["BT"] = "Biotechnology"
        this["CC"] = "Computer Aided Design and Manufacturing"
        this["CH"] = "Chemical Engineering (M.Tech)"
        this["CM"] = "Communication Systems"
        this["CS"] = "Computer Science and Engineering (M.Tech.)"
        this["DN"] = "Design Engineering"
        this["DS"] = "Computer Science & Engineering in Artificial Intelligence & Data Science"
        this["EE"] = "Control & Instrumentation"
        this["EG"] = "Environmental Geotechnology"
        this["EL"] = "Digital Systems"
        this["EM"] = "Engineering Mechanics and Design"
        this["EN"] = "Civil Engineering (Environmental Engineering)"
        this["FE"] = "Fluids Engineering"
        this["GI"] = "Geoinformatics"
        this["GT"] = "Civil Engineering (Geotechnical Engineering)"
        this["IS"] = "Information Security"
        this["MT"] = "Material Science and Engineering"
        this["PD"] = "Product Design and Development"
        this["PE"] = "Power Electronics and Drives"
        this["PR"] = "Production Engineering"
        this["PS"] = "Power System"
        this["PTCC"] = "Mechanical Engineering (Computer Aided Design and Manufacturing - Part Time)"
        this["PTEE"] = "Electrical Engineering (Control And Instrumentation - Part Time)"
        this["PTEL"] = "Electronics Engineering (Digital System - Part Time)"
        this["PTEN"] = "Civil Engineering (Environmental Engineering Part Time)"
        this["PTPE"] = "Power Electronics and A.S.I.C. Design (Part Time)"
        this["PTPR"] = "Production Engineering (Part Time)"
        this["PTST"] = "Civil (Structural) Engineering (Part Time)"
        this["PTSW"] = "Software Engineering (Part Time)"
        this["SP"] = "Signal Processing"
        this["ST"] = "Structural Engineering"
        this["SW"] = "Software Engineering"
        this["TH"] = "Thermal Engineering"
        this["TR"] = "Transportation Engineering"
        this["VL"] = "Microelectronics and VLSI Design"

        // Ph.D.
        this["RAM"] = "Doctor of Philosophy - Applied Mechanics Department"
        this["RBT"] = "Doctor of Philosophy - Biotechnology Department"
        this["RCE"] = "Doctor of Philosophy - Civil Engineering Department"
        this["RCH"] = "Doctor of Philosophy - Chemistry Department"
        this["RCL"] = "Doctor of Philosophy - Chemical"
        this["RCS"] = "Doctor of Philosophy - Computer Science and Engineering Department"
        this["RCY"] = "Doctor of Philosophy - Chemistry Department"
        this["REE"] = "Doctor of Philosophy - Electrical Engineering Department"
        this["REL"] = "Doctor of Philosophy - Electronics and Communication Engineering Department"
        this["RGI"] = "Doctor Of Philosophy - GIS and Remote Sensing"
        this["RHU"] = "Doctor of Philosophy - Humanities and Social Sciences"
        this["RMA"] = "Doctor of Philosophy - Mathematics Department"
        this["RME"] = "Doctor of Philosophy - Mechanical Engineering Department"
        this["RMS"] = "Doctor of Philosophy - Management"
        this["RPH"] = "Ph.D. (Physics Department)"
    }

    /**
     * Extracts the [Course] by matching [COURSE_CODE_REGEX] to param [regNo]
     */
    fun extract(regNo: String): Course = COURSE_CODE_REGEX
        .find(regNo.uppercase())
        .let { match ->
            match ?: return Course(stream = UNIDENTIFIED, branch = "")
            Course(
                stream = getStream(courseCode = match.value),
                branch = COURSE_MAP.getOrDefault(match.value, UNIDENTIFIED)
            )
        }

    private fun getStream(courseCode: String): String = when (courseCode[0]) {
        in '0'..'9' -> "B.Tech."
        'R' -> "Ph.D."
        else -> when (courseCode) {
            "MSC" -> "M.Sc."
            "MB" -> "MBA"
            "CA" -> "MCA"
            else -> "M.Tech."
        }
    }
}
